package com.bassis.bean.event;

import com.bassis.bean.BeanFactory;
import com.bassis.tools.reflex.Reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ApplicationEventPublisher {

    private static class LazyHolder {
        private static final ApplicationEventPublisher INSTANCE = new ApplicationEventPublisher();
    }

    private ApplicationEventPublisher() {
    }

    public static final ApplicationEventPublisher getInstance() {
        return ApplicationEventPublisher.LazyHolder.INSTANCE;
    }

    private final static Map<ApplicationEvent, Set<ApplicationListener>> listeners = new HashMap<>();

    /**
     * 添加事件
     *
     * @param listener 要添加的事件
     */
    public void addListener(ApplicationListener listener) {
        System.out.println(Reflection.getInterfaceT(listener.getClass(), 1).getName());
//        listeners.add(listener);
    }

    /**
     * 移除事件
     *
     * @param listener 要移除的事件
     */
    public void removeListener(ApplicationListener listener) {
        listeners.remove(listener);
    }

    /**
     * 触发事件
     *
     * @param event
     */
    public void publishEvent(ApplicationEvent event) {
        if (listeners.containsKey(event)) {
            Set<ApplicationListener> listenerSet = listeners.get(event);
        }
        this.notifyListeners(event);
    }

    /**
     * 通知所有的Listener
     */
    private void notifyListeners(ApplicationEvent event) {
//        listeners.forEach(listener -> {
//            listener.onApplicationEvent(event);
//        });
    }
}
