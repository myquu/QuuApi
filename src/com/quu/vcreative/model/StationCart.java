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
public class StationCart {

	@JsonProperty
	private String station;
	
	@JsonProperty("carts")
	private List<String> cartList;
	
	//This is not passed from controller. Its used internally
	private int stationId;
}
