package com.quu.watermark.service;


import com.quu.watermark.model.CampaignIn;
import com.quu.watermark.model.CampaignOut;
import com.quu.watermark.model.CampaignQId;


public interface ICampaignService {

	public CampaignOut save(CampaignIn campaignIn);

	public int assignQIds(CampaignQId campaignQId);
	
	public int unassignQIds(CampaignQId campaignQId);
	
    public int deactivate(int id);
	
}
