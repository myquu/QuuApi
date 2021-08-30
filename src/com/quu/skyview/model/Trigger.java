package com.quu.skyview.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class Trigger {

	@JsonProperty("StationID")
	private int stationId;
	@JsonProperty("EventID")
	private int eventId;
	@JsonProperty("BreakID")
	private int breakId;
	
}
