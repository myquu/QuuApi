package com.quu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class StationInput {
	private String type;
    private String ip;
    private int port;
}
