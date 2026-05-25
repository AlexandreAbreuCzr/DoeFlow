package com.alexandre.doeflow.infra.exceptions.campaign;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(String detalhes) {
        super(detalhes);
    }

    public CampaignNotFoundException(){
        super("Campaign not found");
    }
}
