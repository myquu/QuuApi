package com.quu.skyview.service;


import java.util.List;
import java.util.Map;

import com.quu.skyview.model.CampaignWatermarks;
import com.quu.skyview.model.Watermark;

public interface IWatermarkService {

	public int assign(CampaignWatermarks campaignWatermarks);
	
	public int unassign(CampaignWatermarks campaignWatermarks);
	    	
	public Map<Integer, List<Watermark>> audit();
}
