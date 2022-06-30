package com.bulentyilmaz.todoapp.exception;

public class UnauthorizedException extends BusinessException{
    public UnauthorizedException(String message) {
        super(ErrorCode.unauthorized, message);
    }
}
