package com.quu.skyview.dao;

import java.util.List;

import com.quu.skyview.model.Campaign;


public interface ICampaignDAO {

	public List<Campaign> getAll(String IMAGENAME);
	
	public Campaign get(int id, String IMAGENAME);
	
	public int campaignExists(int id);
	
	public int[] save(Campaign campaign, String IMAGENAME);

	public void active(int id);
	
    public void delete(int id);
    
}
