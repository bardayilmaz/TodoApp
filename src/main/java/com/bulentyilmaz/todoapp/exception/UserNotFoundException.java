package com.bulentyilmaz.todoapp.exception;

public class UserNotFoundException extends BusinessException{
    public UserNotFoundException(String message) {
        super(ErrorCode.user_missing, message);
    }
}
