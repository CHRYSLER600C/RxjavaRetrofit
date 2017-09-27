package com.born.frame.dataclass.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NameValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("message")
	public String name;
	@Expose
	@SerializedName("code")
	public String value;

	public NameValue(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

}