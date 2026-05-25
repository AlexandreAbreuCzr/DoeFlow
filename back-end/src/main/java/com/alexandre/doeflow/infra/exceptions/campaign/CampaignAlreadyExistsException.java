package com.alexandre.doeflow.infra.exceptions.campaign;

public class CampaignAlreadyExistsException extends RuntimeException {
    public CampaignAlreadyExistsException(String detalhes) {
        super(detalhes);
    }

    public CampaignAlreadyExistsException(){
        super("Campaign already exists");
    }
}
