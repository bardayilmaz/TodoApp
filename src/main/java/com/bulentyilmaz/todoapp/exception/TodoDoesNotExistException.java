package com.bulentyilmaz.todoapp.exception;

public class TodoDoesNotExistException extends BusinessException{

    public TodoDoesNotExistException(String message) {
        super(ErrorCode.todo_missing, message);
    }
}
