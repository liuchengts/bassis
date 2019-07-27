package com.bassis.bean.event;

import com.bassis.tools.reflex.Reflection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationEventPublisher {

    private static class LazyHolder {
        private static final ApplicationEventPublisher INSTANCE = new ApplicationEventPublisher();
    }

    private ApplicationEventPublisher() {
    }

    public static final ApplicationEventPublisher getInstance() {
        return ApplicationEventPublisher.LazyHolder.INSTANCE;
    }

    private final static Map<Class<?>, Set<ApplicationListener>> listeners = new ConcurrentHashMap<>();

    /**
     * 添加事件
     *
     * @param listener 要添加的事件
     */
    public static synchronized void addListener(ApplicationListener listener) {
        Class<?> aclass = Reflection.getInterfaceT(listener.getClass(), 1);
        Set<ApplicationListener> listenerSet = new HashSet<>();
        if (listeners.containsKey(aclass)) {
            listenerSet = listeners.get(aclass);
        }
        listenerSet.add(listener);
        listeners.put(aclass, listenerSet);
    }

    /**
     * 移除事件
     *
     * @param listener 要移除的事件
     */
    public static synchronized void removeListener(ApplicationListener listener) {
        Class<?> aclass = Reflection.getInterfaceT(listener.getClass(), 1);
        if (!listeners.containsKey(aclass)) return;
        Set<ApplicationListener> listenerSet = listeners.get(aclass);
        if (!listenerSet.contains(listener)) return;
        listenerSet.remove(listener);
        if (listenerSet.isEmpty()) {
            listeners.remove(aclass);
        } else {
            listeners.put(aclass, listenerSet);
        }
    }

    /**
     * 触发事件
     *
     * @param event
     */
    public static void publishEvent(ApplicationEvent event) {
        Class<?> aclass = event.getClass();
        if (!listeners.containsKey(aclass)) return;
        Set<ApplicationListener> listenerSet = listeners.get(aclass);
        notifyListeners(listenerSet, event);
    }

    /**
     * 通知所有的Listener
     */
    private static void notifyListeners(Set<ApplicationListener> listenerSet, ApplicationEvent event) {
        listenerSet.forEach(listener -> {
            listener.onApplicationEvent(event);
        });
    }
}
