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

	private static final String IMAGENAME = "logo.jpeg";
	
	@Inject
    private ICampaignDAO campaignDAO;
		

    @Override
    public List<Campaign> getAll() {

        return campaignDAO.getAll();
    }
    
    @Override
    public Campaign get(int id) {
        
        return campaignDAO.get(id);
    }
    
    //Adds (-1)/Updates (<> -1) a campaign. If a Base64 string(image) is passed, we convert it to an image file and save it on imageserver. To save it we need the campaign id.
    /**
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
    	
    	
    	if(campaign.getImage() != null)
    	{
    		campaign.setImageName(IMAGENAME);
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
    	
    	int[] ret = campaignDAO.save(campaign);
    	
    	if(ret != null)
    	{
    		int lineItemId = ret[0],
				active = ret[1];
    		
	    	if(campaign.getImage() != null)
	    	{
		    	new Thread(() -> {
			    	
		    		//Handle image base64 to url conversion and save on imageserver
		    		Map<String, String> params = new HashMap<String, String>();
		    		params.put("imagePath", "campaign_images/" + lineItemId + "/logo");
		    		params.put("name", IMAGENAME);
		    		params.put("base64String", campaign.getImage());
		    		
		    		Util.getWebResponse(Constant.BASE64STRINGTOIMAGESERVICE_URL, params, false);
		        	
			    }).start();
	    	}
	    	
	    	//active status
	    	if(active == 1)
	    	{
	    		Util.clearQuuRDSCache();
	    	}
	    	
	    	return lineItemId;
    	}
    	
    	return -2;
    }
    
    /**
     * Returns 1 - updated rows (OK), -1 - Not a valid SV campaign, -2 - DB error.
     */
    @Override
    public int deactivate(int id) {
        
    	int v = campaignDAO.campaignExists(id);
		
		if(v == -1)
		{			
			return -1;
		}
		
    	int ret = campaignDAO.deactivate(id);
    	
    	if(ret == 1)
    	{
    		Util.clearQuuRDSCache();
    	}
    	
    	return ret;
    }
    
    /**
     * Returns 1 - deleted row (OK), -1 - Not a valid SV campaign, -2 - DB error.
     */
    @Override
    public int delete(int id) {
        
    	int v = campaignDAO.campaignExists(id);
		
		if(v == -1)
		{			
			return -1;
		}
		
    	int ret = campaignDAO.delete(id);
    	
    	if(ret == 1)
    	{
    		Util.clearQuuRDSCache();
    	}
    	
    	return ret;
    }
}
