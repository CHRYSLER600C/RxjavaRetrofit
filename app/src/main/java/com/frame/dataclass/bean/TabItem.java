package com.frame.dataclass.bean;

import java.io.Serializable;

public class TabItem implements Serializable {
	private static final long serialVersionUID = 1L;

	public String content;
	public String key;

	public TabItem(String content, String key) {
		this.content = content;
		this.key = key;
	}
}