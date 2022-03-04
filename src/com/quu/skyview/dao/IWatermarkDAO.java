package com.quu.skyview.dao;

import java.util.List;
import java.util.Map;

public interface IWatermarkDAO {

	public int assign(int campaignId, String watermarkIds);
	
	public int unassign(int campaignId, String watermarkIds);
	    	
	public List<String> audit(String campaignIds);
}
