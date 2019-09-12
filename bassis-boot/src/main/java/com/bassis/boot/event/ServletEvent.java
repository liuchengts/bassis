package com.bassis.boot.event;

import com.bassis.bean.event.ApplicationEvent;

import java.util.Map;

/**
 * Servlet 初始化完成事件通知
 */
public class ServletEvent extends ApplicationEvent {
    public ServletEvent(Object source) {
        super(source);
    }
}
