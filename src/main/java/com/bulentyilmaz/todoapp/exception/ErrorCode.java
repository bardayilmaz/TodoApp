package com.bulentyilmaz.todoapp.exception;

public enum ErrorCode {

    unknown(400),
    forbidden(403),
    account_not_verified(403),
    resource_missing(404),
    todo_missing(404),
    already_onboarded(409),
    conflict(409),
    code_mismatch(409),
    already_submitted(409),
    validation(422);

    private final int httpCode;

    ErrorCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}