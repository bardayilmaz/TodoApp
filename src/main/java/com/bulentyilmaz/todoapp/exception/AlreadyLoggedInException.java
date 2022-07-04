package com.bulentyilmaz.todoapp.exception;

public class AlreadyLoggedInException extends BusinessException{
    public AlreadyLoggedInException(String message) {
        super(ErrorCode.conflict, message);
    }
}
