package com.me.warepulse.exception;

import lombok.Getter;

@Getter
public class WarePulseException extends RuntimeException {

    private final ErrorCode errorCode;

    public WarePulseException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
