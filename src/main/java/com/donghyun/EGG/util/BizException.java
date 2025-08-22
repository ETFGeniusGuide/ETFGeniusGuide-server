package com.donghyun.EGG.util;

public class BizException extends RuntimeException {
    private final String code;
    public BizException(String code, String message) {
        super(message); this.code = code;
    }
    public String getCode() { return code; }

    public static BizException notFound(String msg) { return new BizException("NOT_FOUND", msg); }
    public static BizException conflict(String msg) { return new BizException("CONFLICT", msg); }
    public static BizException invalid(String msg) { return new BizException("INVALID", msg); }
    public static BizException forbidden(String msg) { return new BizException("FORBIDDEN", msg); }
}
