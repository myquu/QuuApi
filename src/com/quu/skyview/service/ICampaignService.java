package com.quu.skyview.service;

import java.util.List;

import com.quu.skyview.model.Campaign;


public interface ICampaignService {

	public List<Campaign> getAll();

	public Campaign get(int id);	
	
    public int save(Campaign campaign);

    public int deactivate(int id);
	
    public int delete(int id);
    
}
