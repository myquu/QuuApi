package com.quu.vcreative.dao;

import java.util.List;

import com.quu.vcreative.model.CampaignIn;


public interface ICampaignDAO {

	public int add(CampaignIn campaign);

    public int[] update(CampaignIn campaign);

    public int deactivate(int id);
    
    public int campaignExists(int id);
    
    public void assignStations(int id, String station_ids);
    
    public void saveTraffic(int id, int station_id, String carts);
	
}
