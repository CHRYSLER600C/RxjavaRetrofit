package com.frame.dataclass.bean;

import java.io.Serializable;

public class NameValue implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String value;

    public NameValue(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

}