package com.quu.vcreative.service;

import java.util.List;

import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignOut;
import com.quu.vcreative.model.CampaignStationIn;
import com.quu.vcreative.model.ImageIn;
import com.quu.vcreative.model.LineItemIn;


public interface ICampaignService {

	public CampaignOut save(CampaignIn campaignIn);

    public int assignImage(ImageIn imageIn);
    
    public String[] assignStationsCarts(CampaignStationIn campaignStation);
    
    public int deactivate(int id);
	
}
