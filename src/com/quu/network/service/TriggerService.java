package com.quu.network.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.quu.dao.IQuuDAO;
import com.quu.model.Station;
import com.quu.model.StationInput;
import com.quu.network.dao.ICampaignDAO;
import com.quu.network.model.Campaign;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@RequestScoped
public class TriggerService implements ITriggerService{

	@Inject
    private ICampaignDAO campaignDAO;
		

    @Override
    public int sendCampaigns(String json) {
        
    	try
    	{
	    	JSONObject obj = new JSONObject(json);
			
			int campaignId = obj.optInt("CampaignID");  //Will give 0 if CampaignID is null
			//JSONObject js = obj.optJSONObject("js");
	    	
			if(campaignId > 0)
			{
		    	Map<String, Station> stationMap = Scheduler.StationMap;
		    	
		    	String callLetters = "KRAP-AM";
		    	
		    	Station station = stationMap.get(callLetters);
		    	
		    	if(station != null)
		    	{
			    	List<StationInput> inputList = station.getInputList();
			    	
			    	for(StationInput input : inputList)
			    	{
			    		if(input.getType().equals("Network"))
			    		{
			    			Campaign campaign = campaignDAO.get(campaignId);
			    	    	
			    	    	JSONObject objE = createEvent(campaign, 30000);
			    	    	
			    	    	//input.getIp() will be the external IP for Network type. "170.39.241.151"
			    	    	Util.sendToTCPServer(input.getPort(), input.getIp(), objE.toString());
			    	    	//System.out.println("TriggerService 333: " + objE.toString());
			    	    	return 1;
			    		}
			    	}
		    	}
			}
    	}
    	catch(JSONException ex) {
    		return -1;
		}
    	
    	return -1;
    }
    
    
    //Rivendell format
    //The only fields that matter are cart and category(groupName).
    private JSONObject createEvent(Campaign campaign, int duration)
    {
    	JSONObject obj = new JSONObject();
    	
    	try {
    		JSONObject nowObj = new JSONObject();
    		nowObj.put("cartNumber", campaign.getId());
	    	nowObj.put("groupName", "COM");
	    	nowObj.put("artist", campaign.getLine1());
	    	nowObj.put("title", campaign.getLine2());
	    	nowObj.put("length", duration);
	    	
	    	JSONObject padUpdateObj = new JSONObject();
	    	padUpdateObj.put("now", nowObj);
	    	obj.put("padUpdate", padUpdateObj);
    	}
    	catch(JSONException ex) {}
    	
    	return obj;
    }
    
}
