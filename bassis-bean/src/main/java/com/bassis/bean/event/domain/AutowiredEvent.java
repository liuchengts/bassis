package com.bassis.bean.event.domain;

import com.bassis.bean.event.ApplicationEvent;
/**
 * 资源加载完成 可以开始进行注入时的通知
 */
public class AutowiredEvent extends ApplicationEvent {
    public AutowiredEvent(Object source) {
        super(source);
    }
}
