package com.quu.skyview.service;


import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.skyview.dao.ICampaignDAO;
import com.quu.skyview.dao.IWatermarkDAO;
import com.quu.skyview.model.CampaignWatermarks;
import com.quu.skyview.model.Watermark;


@RequestScoped
public class WatermarkService implements IWatermarkService{

	@Inject
    private IWatermarkDAO watermarkDAO;
	@Inject
    private ICampaignDAO campaignDAO;
	

    public int assign(CampaignWatermarks campaignWatermarks)
    {
    	int ret = campaignDAO.campaignExists(campaignWatermarks.getId());
    	
    	//If its a valid campaign, assign watermarks to it.
    	if(ret == 1)
    	{
    		for(Watermark watermark : campaignWatermarks.getWatermarkList())
    		{
    			watermarkDAO.assign(campaignWatermarks.getId(), watermark.getQid(), watermark.getDuration());
    		}
    		
    		campaignDAO.active(campaignWatermarks.getId());
    	}
    	
    	return ret;
    }
    
    public int unassign(CampaignWatermarks campaignWatermarks)
    {
    	int ret = campaignDAO.campaignExists(campaignWatermarks.getId());
    	
    	//If its a valid campaign, unassign watermarks from it.
    	if(ret == 1)
    	{
    		StringJoiner sb = new StringJoiner(",");
    		
    		for(Watermark watermark : campaignWatermarks.getWatermarkList())
    		{
    			sb.add(watermark.getQid());
    		}
    		
    		watermarkDAO.unassign(campaignWatermarks.getId(), sb.toString());
    	}
    	    	   	
    	return ret;
    }
    
    public Map<Integer, List<Watermark>> audit()
    {
    	return watermarkDAO.audit();
    }
}
