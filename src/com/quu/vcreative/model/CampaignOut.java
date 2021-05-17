package com.quu.vcreative.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
@AllArgsConstructor
@Getter
@Setter

public class CampaignOut {

	@JsonProperty("VC_POID")
	private String VC_POID;
	
	//@JsonProperty("Q_Ad_ID")
	//private int orderId;
	
	@JsonProperty("Q_Campaign_ID")
	private int id;  //Line item id
	
	@JsonProperty("Q_Ad_URL")
	private String previewUrl;
	
	/* These will be in the response from assign
	@JsonProperty("Q_station_not_enabled")
	private String stationsNotEnabled;
	
	@JsonProperty("Q_station_req_approval")
	private String stationsReqApproval;
	*/
}