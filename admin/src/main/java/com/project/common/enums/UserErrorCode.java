package com.project.common.enums;

import com.project.common.convention.errorcode.IErrorCode;

public enum UserErrorCode implements IErrorCode {
    USER_NUll("A000002", "用户不存在");
    private final String code;

    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
