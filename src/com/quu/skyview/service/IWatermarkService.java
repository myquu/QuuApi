package com.quu.skyview.service;

import java.util.List;
import java.util.Map;

import com.quu.skyview.model.CampaignWatermarks;

public interface IWatermarkService {

	public int assign(CampaignWatermarks campaignWatermarks);
	
	public int unassign(CampaignWatermarks campaignWatermarks);
	    	
	public Map<String, List<String>> audit(List<String> campaignIds);
}
