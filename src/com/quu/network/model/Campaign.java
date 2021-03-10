package com.quu.network.model;

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
//@RequiredArgsConstructor  //picks only those fields that are marked @NonNull
@AllArgsConstructor
@Getter
@Setter
//@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
//Use @XmlTransient on a property to skip marshalling or unmarshalling
/*
public class Campaign {

	@JsonProperty("ID")
	private int id;
	@JsonProperty("SkyviewID")
	private int skyviewId;
	@JsonProperty("Line1")
	private String line1;
	@JsonProperty("Line2")
	private String line2;
	@JsonProperty("Image")
	private String image;
		
}
*/
public class Campaign {

	@JsonProperty("ID")
	private Integer id;
	@JsonProperty("SkyviewID")
	private Integer networkId;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Line1")
	private String line1;
	@JsonProperty("Line2")
	private String line2;
	//Image is a Base64String POSTed to us. It is not sent out.
	@JsonProperty(value = "Image", access = Access.WRITE_ONLY)  //the property may only be written (set) for deserialization, but will not be read (get) on serialization, that is, the value of the property is not included in serialization.
	private String image;
	//The image name is not passed from controller. It is a constant that will be saved only if image param comes with a value from controller.
	private String imageName;
	
}