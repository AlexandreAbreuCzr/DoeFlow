package com.alexandre.doeflow.infra.exceptions.donation;

public class InvalidDonationException extends RuntimeException {

    public InvalidDonationException(String message) {
        super(message);
    }
}
