package com.frame.dataclass.bean;

/**
 * Event事件类
 */

public class Event {

    private String action;
    private Object data;


    public Event(String action) {
        this.action = action;
    }

    public Event(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public Object getData() {
        return data;
    }
}
