package com.quu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//This class is used with createBillboardsSchedules.

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) 
public class BBSchedulesIn {

	private int campaignID;
    @JsonProperty("schedules")	
   	private List<BBScheduleIn> scheduleList;
		
}
