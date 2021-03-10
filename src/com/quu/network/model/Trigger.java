package com.quu.network.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class Trigger {

	@JsonProperty("EventID")
	private int eventId;
	@JsonProperty("BreakID")
	private int breakId;
	
}
