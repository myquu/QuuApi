package com.quu.vcreative.dao;

import java.util.List;

import com.quu.vcreative.model.CampaignIn;


public interface ICampaignDAO {

	public int add(CampaignIn campaign);

    public int update(CampaignIn campaign);

    public void assignStations(int id, String station_ids);
    
    public int deactivate(int id);
	
}
