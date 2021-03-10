package com.quu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.quu.model.Station;
import com.quu.service.IQuuService;


public class Scheduler 
{
	@Inject
    private static IQuuService quuService;
	
	//Key: callLetters, Value: Station obj
	public static Map<String, Station> StationMap;
	
	//Refresh the cached data.
    static 
    {
    	//Calculate the minutes between now and 21:00:00
    	Long initialDelay = LocalDateTime.now().until(LocalDate.now().atTime(21, 1, 0), ChronoUnit.MINUTES);
    	
    	//If 21:0:00 is already passed then set initial delay to that time next day.
    	if(initialDelay < 0)
    	{
    		initialDelay = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(21, 1, 0), ChronoUnit.MINUTES);
    	}
    		
    	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    	scheduler.scheduleAtFixedRate(() -> {
    		Scheduler.StationMap = quuService.getStations(); 
    		System.out.println("QuuAPI cache refreshed!");
		}, initialDelay, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    	
    	scheduler.shutdown();
    }
    
}
