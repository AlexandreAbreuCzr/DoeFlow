package com.alexandre.doeflow.infra.exceptions.donation;

public class DonationNotFoundException extends RuntimeException {

    public DonationNotFoundException() {
        super("Donation not found");
    }
}
