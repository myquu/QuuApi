package com.quu.vcreative.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter
public class CampaignStationOut {

	@JsonProperty("Q_station_not_found")
	private String stationsNotFound;
	@JsonProperty("Q_station_not_enabled")
	private String stationsNotEnabled;
		
}
