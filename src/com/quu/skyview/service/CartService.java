package com.quu.skyview.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.quu.skyview.dao.ICartDAO;
import com.quu.skyview.model.Schedule;
import com.quu.skyview.model.Segment;
import com.quu.skyview.model.StationCart;
import com.quu.util.Constant;


@RequestScoped
public class CartService implements ICartService{

	@Inject
    private ICartDAO cartDAO;
		
	
	@Override
    public HashMap<String, List<Segment>> getCartSchedules(int stationId, String date) {
        
        return cartDAO.getCartSchedules(stationId, date);
    }
	
	
	@Override
	/*
	public int assignStationCart(StationCart stationCart) 
	{				
		int stationCartId = cartDAO.saveStationCart(stationCart.getStationId(), stationCart.getCart(), stationCart.getStartDate(), stationCart.getEndDate());
		
		if(stationCartId > -1)
		{
			List<Segment> segmentList = stationCart.getSegmentList();
			
			if(segmentList != null)
			{
				List<Integer> campaignIdList = new ArrayList<>();
				
				for(Segment segment : segmentList)
				{
					campaignIdList.add(segment.getCampaignId());
					
					cartDAO.saveStationCartSegment(stationCartId, segment.getOrder(), segment.getCampaignId(), segment.getDuration());
				}
				
				//return cartDAO.assignStationCart(stationCart.getStationId(), stationCart.getCart(), campaignIdList);
			}
		}
		
		return stationCartId;
	}
	*/
	public int assignStationCartDates(StationCart stationCart) 
	{				
		//Create as many records as there are dates between the start and end date including the two dates.
		LocalDate date = LocalDate.parse(stationCart.getStartDate(), Constant.dateFormatter),  //Its the start date to begin with - either equal to or before the end date but it will change in the loop.
				endDate = LocalDate.parse(stationCart.getEndDate(), Constant.dateFormatter);
		
		int ret = 1;  //Will be -1 only if there is an error in inserting any record
		
		do  //For each day in the range
		{
			int stationCartId = cartDAO.saveStationCartDate(stationCart.getStationId(), stationCart.getCart(), date.format(Constant.dateFormatter));
			
			if(stationCartId > -1)
			{
				List<Segment> segmentList = stationCart.getSegmentList();
				
				if(segmentList != null)
				{
					List<Integer> campaignIdList = new ArrayList<>();
					
					for(Segment segment : segmentList)
					{
						campaignIdList.add(segment.getCampaignId());
						
						int status = cartDAO.saveStationCartSegment(stationCartId, segment.getOrder(), segment.getDuration(), segment.getCampaignId(), segment.getReportingID());
						
						if(status == -1)
						{
							ret = -1;
						}
					}
				}
			}
			else
			{
				ret = -1;
			}
			
		    date = date.plus(1, ChronoUnit.DAYS);  //Keep incrementing the start date until it becomes greater than the end date.
		    
		}while (!date.isAfter(endDate));
				
		return ret;
	}

	@Override
	public int updateStationCartDate(StationCart stationCart) 
	{
		//Will be -1 only if there is an error in inserting any record. This is the stationCartId.
		int ret = cartDAO.updateStationCartDate(stationCart.getStationId(), stationCart.getCart(), stationCart.getStartDate());
		
		if(ret > -1)
		{
			List<Segment> segmentList = stationCart.getSegmentList();
			
			//The below check is important. Client can pass a null segmentList to show that the above station-cart-date combo has expired and we should delete all its segments (which happened in updateStationCart()).
			if(segmentList != null)
			{
				List<Integer> campaignIdList = new ArrayList<>();
				
				for(Segment segment : segmentList)
				{
					campaignIdList.add(segment.getCampaignId());
					
					int status = cartDAO.saveStationCartSegment(ret, segment.getOrder(), segment.getDuration(), segment.getCampaignId(), segment.getReportingID());
					
					if(status == -1)
					{
						ret = -1;
					}
				}
			}
		}
		//else  -1 or -2
		
		return ret;
	}
    	
}
