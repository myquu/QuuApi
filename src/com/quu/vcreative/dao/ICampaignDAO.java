package com.quu.vcreative.dao;

import java.util.List;

import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStationDetail;
import com.quu.vcreative.model.ImageIn;
import com.quu.vcreative.model.LineItemIn;


public interface ICampaignDAO {

	public int saveOrder(CampaignIn campaign);

	public int[] saveLineItem(int itemId, LineItemIn lineItem);
	
    public int deactivate(String POID, int id);
    
    public int campaignExists(String POID, int id);
    
    public int[] assignImage(ImageIn imageIn);
    
    public CampaignStationDetail assignStations(String POID, int id, String station_ids);
    
    public int assignStationCarts(int advertiserId, int itemId, int id, String startDate, String endDate, int stationId, String VC_contractno, String carts);
    
    public void deleteStationsCarts(String POID, int id, int stationId, String carts);
    
    public void saveTraffic(int id, int station_id, String carts);
	
}
