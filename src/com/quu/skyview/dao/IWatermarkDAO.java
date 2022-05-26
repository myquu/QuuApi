package com.quu.skyview.dao;


import java.util.List;
import java.util.Map;

import com.quu.skyview.model.Watermark;

public interface IWatermarkDAO {

	public void assign(int campaignId, String watermarkIds, int duration);
	
	public void unassign(int campaignId, String watermarkIds);
	    	
	public Map<Integer, List<Watermark>> audit();
}
