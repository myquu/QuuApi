package com.quu.skyview.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(of = {"id"})  //This is used in cartDAO.getCartSchedules() where it uses indexOf() and indexOf() uses equals().
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationCart {

	@JsonProperty("quuStationId")
	private int stationId;
	@JsonProperty("cart")
	private String cart;
	@JsonProperty("startDate")
	private String startDate;
	@JsonProperty("endDate")
	private String endDate;
	@JsonProperty("segments")
	private List<Segment> segmentList;
		
	//Internal properties
	@JsonProperty(access = Access.WRITE_ONLY)
	private int id;
}
