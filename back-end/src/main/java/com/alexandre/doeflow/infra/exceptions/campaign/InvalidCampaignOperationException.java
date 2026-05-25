package com.alexandre.doeflow.infra.exceptions.campaign;

public class InvalidCampaignOperationException extends RuntimeException {
    public InvalidCampaignOperationException(String detalhes) {
        super(detalhes);
    }

    public InvalidCampaignOperationException(){
        super("Invalid Campaign Operation ");
    }
}
