package com.quu.skyview.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.skyview.dao.ICampaignDAO;
import com.quu.skyview.model.Campaign;
import com.quu.util.Constant;
import com.quu.util.Util;


@RequestScoped
public class CampaignService implements ICampaignService{

	private static final String IMAGENAME = "logo.jpeg";  //The image comes in a a Base64 string without an image name so we set this hardcoded name. We don't store the name in the database.
	
	@Inject
    private ICampaignDAO campaignDAO;
		

    @Override
    public List<Campaign> getAll() {

        return campaignDAO.getAll(IMAGENAME);
    }
    
    @Override
    public Campaign get(int id) {
        
        return campaignDAO.get(id, IMAGENAME);
    }
    
    /**
     * This method adds (-1)/Updates (<> -1) a campaign. 
     * If a Base64 string(image) is passed, we convert it to an image file and save it on imageserver. To save it we need the campaign id.
     * Returns >0 - line item id (OK), -1 - Not a valid SV campaign, -2 - DB error.
     */
    @Override
    public int save(Campaign campaign) {
        
    	//Update
    	if(campaign.getId() != -1)
    	{
    		int v = campaignDAO.campaignExists(campaign.getId());
    		
    		if(v == -1)
    			return -1;
    	}
    	
    	
    	List<String> list = Util.setDPSFields(campaign.getLine1(), campaign.getLine2());
    	
    	if(list != null)
    	{
    		//Populate DPS fields using reflection
    		try 
    		{
    			for(int i=0; i<list.size(); i++)
        		{
	    			PropertyDescriptor pd = new PropertyDescriptor("dps"+(i+1), campaign.getClass());
	    			// Call setter on specified property (setDps1())
	    			pd.getWriteMethod().invoke(campaign, list.get(i));
        		}
		    } 
    		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
    	}
    	
    	int[] ret = campaignDAO.save(campaign, IMAGENAME);
    	
    	if(ret != null)
    	{
    		int id = ret[0],
				active = ret[1];
    		
    		if(!(campaign.getImageHash().equals("0") || campaign.getImageHash().equals("-1")))  //If its neither 0 nor -1 it means we want to insert or update the image value.
	    	{
		    	new Thread(() -> {
			    	
		    		//Handle image base64 to url conversion and save on imageserver
		    		Map<String, String> params = new HashMap<String, String>();
		    		params.put("imagePath", "networkcampaign_images/" + id + "/logo");
		    		params.put("name", IMAGENAME);
		    		params.put("base64String", campaign.getImage());
		    		
		    		Util.getWebResponse(Constant.BASE64STRINGTOIMAGESERVICE_URL, params, false);
		        	
			    }).start();
	    	}
    		//If its a campaign being updated and the image in it needs to be deleted, then delete it from the image server. The column has already been set to null in the save call above.
    		else if(campaign.getImageHash().equals("0") && campaign.getId() != -1)
    		{
    			new Thread(() -> Util.sendGetRequest(Constant.DELETEIMAGESERVICE_URL + "?requestFrom=QuuAPI&imagePath=" + "networkcampaign_images/" + id + "/logo" + "&fileName=" + IMAGENAME, false)).start();
    		}
    		
    		   		
	    	//active status
	    	if(active == 1)
	    	{
	    		new Thread(() -> Util.clearQuuRDSCache()).start();
	    	}
	    	
	    	return id;
    	}
    	
    	return -2;
    }
    
    /**
     * Returns 1 - deleted/deactivated row (OK), -1 - Not a valid SV campaign.
     */
    @Override
    public int delete(int id) {
        
    	int v = campaignDAO.campaignExists(id);
		
		if(v == -1)
		{			
			return -1;
		}
		
    	campaignDAO.delete(id);
    	
    	new Thread(() -> Util.clearQuuRDSCache()).start();
    	    	
    	return 1;
    }
}
