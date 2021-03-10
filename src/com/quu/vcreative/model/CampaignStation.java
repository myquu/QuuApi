package com.quu.vcreative.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter
public class CampaignStation {

	@JsonProperty
	private int id;
	
	@JsonProperty("stationCarts")
	private List<StationCart> stationCartList;
	
}
