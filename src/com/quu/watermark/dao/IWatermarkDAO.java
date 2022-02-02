package com.quu.watermark.dao;


import com.quu.watermark.model.CampaignIn;
import com.quu.watermark.model.CampaignQId;


public interface ICampaignDAO {

	public int[] saveCampaign(CampaignIn campaign);

	public int assignQIds(int campaignId, String watermark_ids);
	
	public int unassignQIds(int campaignId, String watermark_ids);
	
	public int deactivate(int id);
        
}
