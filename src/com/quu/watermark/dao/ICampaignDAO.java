package com.quu.watermark.dao;


import com.quu.watermark.model.CampaignIn;


public interface ICampaignDAO {

	public int[] saveCampaign(CampaignIn campaign);

	public int deactivate(int id);
        
}
