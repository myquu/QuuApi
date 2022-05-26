package com.quu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.quu.dao.QuuDAO;
import com.quu.model.Station;
import com.quu.model.StationMaps;


public class Scheduler 
{
	//@Inject is difficult to make work with a static field. The object is injected when an instance of the dependent is created but here no instance gets created.
	//@Inject
    //private static IQuuService quuService;
	
	public static StationMaps stationMaps;
	
	//Refresh the cached data every 24 hours.
    
	static 
    {
    	LocalDateTime NinePMCurrentDay = LocalDate.now().atTime(21, 1, 0);
    	
    	//Calculate the minutes between now and 21:00:00
    	Long initialDelay = LocalDateTime.now().until(NinePMCurrentDay, ChronoUnit.MINUTES);
    	
    	//If 21:0:00 is already passed then set initial delay to that time next day.
    	if(initialDelay < 0)
    	{
    		initialDelay = LocalDateTime.now().until(NinePMCurrentDay.plusDays(1), ChronoUnit.MINUTES);
    	}
    		
    	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    	scheduler.scheduleAtFixedRate(() -> {
    		
    		stationMaps = new QuuDAO().getStations();
    		    		 
    		//System.out.println("QuuAPI cache refreshed!");
    	}, initialDelay, 24*60, TimeUnit.MINUTES);
    	
    	//scheduler.shutdown();
    }
	
}
