package com.quu.skyview.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.skyview.dao.IWatermarkDAO;
import com.quu.skyview.model.CampaignWatermarks;


@RequestScoped
public class WatermarkService implements IWatermarkService{

	@Inject
    private IWatermarkDAO watermarkDAO;
		

    public int assign(CampaignWatermarks campaignWatermarks)
    {
    	return watermarkDAO.assign(campaignWatermarks.getId(), String.join(",", campaignWatermarks.getQIds()));
    }
    
    public int unassign(CampaignWatermarks campaignWatermarks)
    {
    	return watermarkDAO.unassign(campaignWatermarks.getId(), String.join(",", campaignWatermarks.getQIds()));
    }
    
    public Map<String, List<String>> audit(List<String> campaignIds)
    {
    	Map<String, List<String>> map = new HashMap<String, List<String>>();
    	
    	for(String campaignId : campaignIds)
    	{
    		 map.put(campaignId, watermarkDAO.audit(campaignId));
    	}
    	
    	return map;
    }
}
