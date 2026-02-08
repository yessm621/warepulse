package com.me.adjustmentservice.exception;

import lombok.Getter;

@Getter
public class AdjustmentServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public AdjustmentServiceException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
