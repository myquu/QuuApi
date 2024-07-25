package com.quu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties
public class RTLog
{
	@JsonProperty("Date Time")
	private String timestamp;
	@JsonProperty("Cart No.")
	private String cart_no;
    @JsonProperty("Input Artist")
    private String raw_artist;
    @JsonProperty("Input Title")
    private String raw_title;
    @JsonProperty("Category")
    private String raw_category;
    //The below is rt1+" "+rt2
    @JsonProperty("Quu Text")
    private String rtText;
}
