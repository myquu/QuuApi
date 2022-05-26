package com.quu.skyview.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Watermark {

	@JsonProperty("qid")
	private String qid;
	
	@JsonProperty("duration")
	private int duration;
}
