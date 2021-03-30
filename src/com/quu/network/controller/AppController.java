package com.quu.network.controller;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/skyview")
public class AppController extends Application {

	//Declare this application's controllers here.
	@Override
	public Set<Class<?>> getClasses() {
		
		Set<Class<?>> resources = new HashSet<>();
        
		resources.add(CampaignController.class);
		resources.add(ScheduleController.class);
		resources.add(TriggerController.class);
		
        return resources;
		
	}
		
}
