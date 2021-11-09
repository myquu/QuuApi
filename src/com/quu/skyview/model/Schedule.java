package com.quu.skyview.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule {

	@JsonProperty("StationID")
	private Integer stationId;
	@JsonProperty("Events")
	private List<Event> eventList;   
	
}
