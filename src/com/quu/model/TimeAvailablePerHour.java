package com.quu.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeAvailablePerHour {
	
	@NonNull  //This in conjunction with @RequiredArgsConstructor will make sure that there is a constructor which uses all non null anointed fields.
	private int hour;  //This is only for display. Otherwise hour is the key in the map.
	
	private float other_duration;
	private float ads_duration;
	
}
