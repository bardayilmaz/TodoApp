package com.bulentyilmaz.todoapp.exception;

public class PasswordMismatchException extends BusinessException{
    public PasswordMismatchException(String message) {
        super(ErrorCode.password_mismatch, message);
    }
}
