package com.quu.watermark.controller;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/QID")
public class AppController extends Application {
	
	//Declare this application's controllers here.
	@Override
	public Set<Class<?>> getClasses() {
		
		Set<Class<?>> resources = new HashSet<>();
        resources.add(CampaignController.class);
        return resources;
		
	}
	
}
