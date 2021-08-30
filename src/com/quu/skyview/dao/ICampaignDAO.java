package com.quu.skyview.dao;

import java.util.List;

import com.quu.skyview.model.Campaign;


public interface ICampaignDAO {

	public List<Campaign> getAll();
	
	public Campaign get(int id);
	
	public int campaignExists(int id);
	
	public int[] save(Campaign campaign);

    public int deactivate(int id);
	
    public int delete(int id);
    
}
