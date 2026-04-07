package com.uniquindio.triage_academy.helpers.exception;

import lombok.Getter;

@Getter
public class CustomException extends Exception {

    private int status;

    public CustomException(int statusCode, String mensaje, Throwable t) {
        super(mensaje, t);
        this.status = statusCode;
    }

}
