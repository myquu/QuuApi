package com.quu.network.dao;

import java.util.List;

import com.quu.network.model.Campaign;


public interface ICampaignDAO {

	public List<Campaign> getAll();
	
	public Campaign get(int id);
	
	public int add(Campaign campaign);

    public int[] update(Campaign campaign);

    public int delete(int id);
	
}
