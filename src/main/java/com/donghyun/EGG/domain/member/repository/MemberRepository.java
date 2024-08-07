package com.donghyun.EGG.domain.member.repository;

import com.donghyun.EGG.domain.member.Member;
import io.jsonwebtoken.Claims;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Member findByEmail(String email);
}
