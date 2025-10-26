-- =========================================================
-- 0) 공통: updated_at 자동 갱신 트리거
-- =========================================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at := NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- =========================================================
-- 1) 사용자 (JWT의 sub 값을 user_id로 사용)
--    - 외부 인증/JWT만 쓸 경우에도 참조 무결성을 위해 보유 권장
-- =========================================================
CREATE TABLE app_user (
                          id           BIGSERIAL PRIMARY KEY,
                          external_sub TEXT UNIQUE NOT NULL, -- JWT 'sub' (고유)
                          email        TEXT,                 -- 선택(표시용)
                          created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER app_user_set_updated_at
    BEFORE UPDATE ON app_user
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


-- =========================================================
-- 2) 카테고리
--    - 시스템 기본 카테고리: user_id NULL
--    - 사용자 정의 카테고리: user_id = 소유자 id
--    - 이름 중복 방지(대소문자/공백 정규화): normalized_name 생성 컬럼 + 유니크 인덱스
-- =========================================================
CREATE TABLE category (
                          id               BIGSERIAL PRIMARY KEY,
                          user_id          BIGINT REFERENCES app_user(id) ON DELETE CASCADE,
                          name             TEXT NOT NULL,
                          normalized_name  TEXT GENERATED ALWAYS AS (lower(trim(name))) STORED,
                          is_custom        BOOLEAN NOT NULL DEFAULT FALSE,
                          is_active        BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- 시스템 카테고리는 user_id IS NULL + is_custom=false 가 자연스러움
                          CONSTRAINT category_name_nonempty CHECK (length(trim(name)) > 0)
);

-- 사용자 범위별 + 시스템 범위별 이름 고유성 보장
-- NULL은 유니크 인덱스에서 서로 다르게 취급되므로 COALESCE로 정규화
CREATE UNIQUE INDEX ux_category_owner_normalized_name
    ON category (COALESCE(user_id, 0), normalized_name);

CREATE INDEX ix_category_user_active ON category (user_id, is_active);

CREATE TRIGGER category_set_updated_at
    BEFORE UPDATE ON category
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


-- =========================================================
-- 3) 월 계획 (budget_plan)
--    - year_month 는 그 달의 1일 DATE로 보관 (YYYY-MM-01)
--    - 사용자별 year_month 유니크
-- =========================================================
CREATE TABLE budget_plan (
                             id           BIGSERIAL PRIMARY KEY,
                             user_id      BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                             year_month   DATE   NOT NULL,  -- YYYY-MM-01 로 보관
                             amount       NUMERIC(14,0) NOT NULL,  -- 월 목표금액
                             created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                             updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                             CONSTRAINT chk_budget_amount_pos  CHECK (amount > 0),
    -- year_month 는 반드시 그 달의 1일이어야 함
                             CONSTRAINT chk_budget_year_month_firstday CHECK (date_trunc('month', year_month) = year_month)
);

-- 사용자별 동일 month 중복 방지
CREATE UNIQUE INDEX ux_budget_plan_user_month ON budget_plan (user_id, year_month);

CREATE TRIGGER budget_plan_set_updated_at
    BEFORE UPDATE ON budget_plan
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


-- =========================================================
-- 4) 지출 내역 (spend)
--    - 월 독립 관리: year_month 컬럼을 명시 보관
--    - date ∈ [year_month, year_month + 1 month) 제약
--    - 카테고리는 시스템(NULL) 또는 사용자 소유(category.user_id = NULL or = spend.user_id)
-- =========================================================
CREATE TABLE spend (
                          id           BIGSERIAL PRIMARY KEY,
                          user_id      BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                          year_month   DATE   NOT NULL,  -- YYYY-MM-01 (budget_plan.year_month 와 동일 표현)
                          "date"       DATE   NOT NULL,  -- 실제 지출일
                          category_id  BIGINT NOT NULL REFERENCES category(id),
                          amount       NUMERIC(14,2) NOT NULL,  -- 원화면 NUMERIC(14,0) 가능
                          memo         TEXT,                     -- 한 줄 메모 권장(프론트에서 길이 제한)
                          created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                          CONSTRAINT chk_spend_amount_pos CHECK (amount > 0),

    -- 날짜가 해당 year_month 범위 안인지 보장
                          CONSTRAINT chk_spend_date_in_month CHECK (
                              "date" >= year_month
                                  AND "date" < (year_month + INTERVAL '1 month')
                              )
);

-- 성능 인덱스: 월별 조회/정렬, 카테고리별 합계
CREATE INDEX ix_spend_user_month_date_desc  ON spend (user_id, year_month, "date" DESC, id DESC);
CREATE INDEX ix_spend_user_month_category   ON spend (user_id, year_month, category_id);
CREATE INDEX ix_spend_user_date             ON spend (user_id, "date");

CREATE TRIGGER spend_set_updated_at
    BEFORE UPDATE ON spend
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


-- =========================================================
-- 5) 참조 무결성(선택) : spend와 budget_plan 연계
--    - 해당 월 계획이 반드시 있어야만 지출을 허용하려면 FK 추가
--      (app_user + year_month 복합 FK는 별도 유니크 키 필요)
-- =========================================================
-- budget_plan(user_id, year_month)에 유니크가 있으므로 이를 참조하는 FK 생성
ALTER TABLE spend
    ADD CONSTRAINT fk_spend_plan
        FOREIGN KEY (user_id, year_month)
            REFERENCES budget_plan (user_id, year_month)
            ON DELETE CASCADE;


-- =========================================================
-- 6) 카테고리 소유권 무결성(권장) - 제약 함수 (선택)
--    지출의 category_id가 시스템(NULL) 카테고리이거나,
--    같은 user_id 소유 카테고리인지 확인.
--    - 트리거로 검증 (성능/복잡도 고려해 서비스 레이어 검증으로 대체 가능)
-- =========================================================
CREATE OR REPLACE FUNCTION validate_spend_category_owner()
RETURNS trigger AS $$
DECLARE
cat_user_id BIGINT;
BEGIN
SELECT user_id INTO cat_user_id FROM category WHERE id = NEW.category_id;

-- 시스템 카테고리(NULL)는 누구나 사용 가능
IF cat_user_id IS NOT NULL AND cat_user_id <> NEW.user_id THEN
    RAISE EXCEPTION 'Category(%) does not belong to user(%)', NEW.category_id, NEW.user_id
      USING ERRCODE = '23514'; -- check_violation semantics
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER spend_validate_category_owner
    BEFORE INSERT OR UPDATE OF category_id, user_id
                     ON spend
                         FOR EACH ROW EXECUTE FUNCTION validate_spend_category_owner();


-- =========================================================
-- 7) 시스템 기본 카테고리 샘플 (선택)
-- =========================================================
INSERT INTO category (user_id, name, is_custom, is_active)
VALUES
    (NULL, '쿠팡', FALSE, TRUE),
    (NULL, '마트',   FALSE, TRUE),
    (NULL, '온라인쇼핑',   FALSE, TRUE),
    (NULL, '',   FALSE, TRUE),
    (NULL, '문화',   FALSE, TRUE);
