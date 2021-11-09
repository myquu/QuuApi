package com.quu.skyview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@NoArgsConstructor  //This is needed to deserialize from the JSON to object in controller.
//@RequiredArgsConstructor  //picks only those fields that are marked @NonNull
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)  //This is so fields passed from controller but not declared here are ignored.
public class Campaign {

	@JsonProperty("ID")
	private int id;
	//@JsonProperty("SkyviewID")
	//private int SkyviewID;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Line1")
	private String line1;
	@JsonProperty("Line2")
	private String line2;
	//Image is a Base64String POSTed to us. It is not sent out.
	@JsonProperty(value = "Image", access = Access.WRITE_ONLY)  //the property may only be written (set) for deserialization, but will not be read (get) on serialization, that is, the value of the property is not included in output JSON.
	private String image;
	@JsonProperty("ImageHash")
	private String imageHash;
	//The image url is not POSTed. It is only included in output JSON i.e. it will be serialized.
	@JsonProperty(value = "ImageUrl", access = Access.READ_ONLY)  
	private String imageUrl;
	
	//The image name is not passed from controller. It is a constant file name that will be saved only if image param comes with a value from controller.
	@JsonProperty(access = Access.WRITE_ONLY)
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