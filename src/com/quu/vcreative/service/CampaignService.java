package com.quu.vcreative.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.vcreative.dao.ICampaignDAO;
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStation;
import com.quu.vcreative.model.StationCart;
import com.quu.model.Station;
import com.quu.util.Constant;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

	@Inject
    private ICampaignDAO campaignDAO;
		

    //Creates a new campaign. 
    @Override
    public int add(CampaignIn campaign) {
        
    	String imageName = null;
    	String VCImageUrl = campaign.getImageUrl();
    	
    	if(VCImageUrl != null)
    	{
    		imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
    		campaign.setImageName(imageName);
    	}
    	
    	int id = campaignDAO.add(campaign);
    	
    	if(imageName != null)
    	{
    		final String imageNameF = imageName;
    		
	    	new Thread(() -> {
		    	
	    		//Handle image base64 to url conversion and save on image server
	    		//String params = "imagePath=campaign_images/" + id + "/logo&name=" + IMAGENAME + "&base64String=" + campaign.getImage();
	    		Map<String, String> params = new HashMap<String, String>();
	    		params.put("imagePath", "campaign_images/" + id + "/logo");
	    		params.put("name", imageNameF);
	    		params.put("base64String", VCImageUrl);
	        	
	    		Util.getWebResponse(Constant.BASE64STRINGTOIMAGESERVICE_URL, params, false);
	        	
		    }).start();
    	}
    	
    	return id;
    }

    //Updates a campaign. 
    @Override
    public int update(CampaignIn campaign) {
        
    	String imageName = null;
    	String VCImageUrl = campaign.getImageUrl();
    	
    	if(VCImageUrl != null)
    	{
    		imageName = VCImageUrl.substring(VCImageUrl.lastIndexOf("/")+1);
    		campaign.setImageName(imageName);
    	}
    	
    	int id = campaignDAO.update(campaign);
    	
    	if(imageName != null)
    	{
    		final String imageNameF = imageName;
    		
	    	new Thread(() -> {
		    	
	    		//Handle image base64 to url conversion and save on image server
	    		Map<String, String> params = new HashMap<String, String>();
	    		params.put("imagePath", "campaign_images/" + campaign.getId() + "/logo");
	    		params.put("name", imageNameF);
	    		params.put("base64String", VCImageUrl);
	    		
	    		Util.getWebResponse(Constant.BASE64STRINGTOIMAGESERVICE_URL, params, false);
	        	
		    }).start();
    	}
    	
    	return id;
    }
    
    public int assignStationsCarts(CampaignStation campaignStation)
    {
    	String station_ids = "";
    	
    	for(StationCart stationCart : campaignStation.getStationCartList()) 
    	{
    		String callLetters = stationCart.getStation();
    		callLetters = callLetters.toUpperCase().replaceFirst("-FM$", "");
    		
    		Station station = Scheduler.StationMap.get(callLetters);
    		station_ids += station.getId() + ",";
    	}
    	
    	if(!station_ids.isEmpty())
    	{
	    	station_ids = station_ids.substring(0, station_ids.length()-1);  //Remove the last comma
	    	campaignDAO.assignStations(campaignStation.getId(), station_ids);
    	}
    	
    	return -1;
    }
    
    //In the SP we check if the id belongs to the Sky Item. Only then we delete.
    @Override
    public int deactivate(int id) {
        
        return campaignDAO.deactivate(id);
    }
}
