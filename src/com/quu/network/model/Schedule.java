package com.quu.network.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule {

	@JsonProperty(value = "ID", access = Access.WRITE_ONLY)
	private Integer id;
	@JsonProperty("Value")
	private String value;  //Any JSON string can be deserialized into a String 
	
}
