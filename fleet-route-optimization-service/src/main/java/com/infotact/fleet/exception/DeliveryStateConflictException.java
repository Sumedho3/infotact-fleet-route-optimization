package com.infotact.fleet.exception;

public class DeliveryStateConflictException extends RuntimeException {
    public DeliveryStateConflictException(String message) {
        super(message);
    }
}