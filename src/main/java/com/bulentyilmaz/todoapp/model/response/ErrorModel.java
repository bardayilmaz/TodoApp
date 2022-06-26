package com.bulentyilmaz.todoapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ErrorModel {

    private int statusCode;
    private String errorCode;
    private String message;
}
