package com.me.receiveservice.exception;

import lombok.Getter;

@Getter
public class ReceiveServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ReceiveServiceException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
