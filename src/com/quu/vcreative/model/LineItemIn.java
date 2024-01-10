package com.quu.vcreative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonIgnoreProperties(ignoreUnknown = true)  //This is so fields passed from controller but not declared here are ignored.
public class LineItemIn {

	@JsonProperty("Q_LineItem_ID")  //@JsonProperty("RDS_LineItem_ID")
	private int id;  
	
	@JsonProperty("VC_LineItem_ID")
	private String VC_LineItem_ID;
	
	@JsonProperty("VC_campaign")
	private String name;  //Line item name
		
	@JsonProperty("VC_line1")
	private String line1;
	
	@JsonProperty("VC_line2")
	private String line2;
	
	@JsonProperty("VC_startDate")
	private String startDate;
	
	@JsonProperty("VC_endDate")
	private String endDate;
	
	@JsonProperty("VC_submitter_email")
	private String email;
	
	@JsonProperty("VC_imageUrl")
	private String imageUrl;
	
	//The image name is not passed from controller. It is a constant that will be saved only if image param comes with a value from controller.
	private String imageName;
	
	//The 8 dps fields are not passed from controller. 
	private String dps1;
	private String dps2;
	private String dps3;
	private String dps4;
	private String dps5;
	private String dps6;
	private String dps7;
	private String dps8;
}