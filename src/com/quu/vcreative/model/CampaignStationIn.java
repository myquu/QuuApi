package com.quu.vcreative.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CampaignStationIn {

	@JsonProperty
	private String VC_POID;
	
	@JsonProperty("Q_LineItem_ID")
	private int id;
	
	@JsonProperty("VC_station_cartID")
	private List<StationCart> stationCartList;
	
}
