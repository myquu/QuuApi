package com.quu.vcreative.service;

import java.util.List;

import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStationIn;


public interface ICampaignService {

	public int add(CampaignIn campaign);

    public int update(CampaignIn campaign);
    
    public String[] assignStationsCarts(CampaignStationIn campaignStation);
    
    public int deactivate(int id);
	
}
