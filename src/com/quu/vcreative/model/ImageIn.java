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
public class ImageIn {

	@JsonProperty
	private String VC_POID;
	
	@JsonProperty("VC_LineItem_ID")
	private String VC_LineItem_ID;
	
	@JsonProperty("Q_LineItem_ID")  //@JsonProperty("RDS_LineItem_ID")  
	private int id; 
	
	@JsonProperty("VC_imageUrl")
	private String imageUrl;
	
	//The image name is not passed from controller. It is a constant that will be saved only if image param comes with a value from controller.
	private String imageName;
		
}