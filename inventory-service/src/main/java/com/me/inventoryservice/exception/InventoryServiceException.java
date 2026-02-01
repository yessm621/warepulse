package com.me.inventoryservice.exception;

import lombok.Getter;

@Getter
public class InventoryServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public InventoryServiceException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
