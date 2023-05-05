package com.serverless.message;

import org.json.simple.JSONObject;

import java.util.List;

public class ResponseOut {

	private final String message;

	private final List<JSONObject> output;

	public ResponseOut(String message, List<JSONObject> output) {
		this.message = message;
		this.output = output;
	}

	public String getMessage() {
		return this.message;
	}

	public List<JSONObject> getOutput() {
		return output;
	}

}
