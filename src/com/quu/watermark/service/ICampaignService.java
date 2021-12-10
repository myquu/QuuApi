package com.quu.watermark.service;


import com.quu.watermark.model.CampaignIn;
import com.quu.watermark.model.CampaignOut;


public interface ICampaignService {

	public CampaignOut save(CampaignIn campaignIn);

    public int deactivate(int id);
	
}
