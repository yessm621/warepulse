package com.me.shipmentservice.exception;

import lombok.Getter;

@Getter
public class ShipmentServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ShipmentServiceException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
