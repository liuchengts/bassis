package com.bassis.bean.event;

import com.bassis.tools.reflex.Reflection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ApplicationEventPublisher {

    private static class LazyHolder {
        private static final ApplicationEventPublisher INSTANCE = new ApplicationEventPublisher();
    }

    private ApplicationEventPublisher() {
    }

    public static  ApplicationEventPublisher getInstance() {
        return ApplicationEventPublisher.LazyHolder.INSTANCE;
    }

    private final static Map<Class<?>, CopyOnWriteArraySet<ApplicationListener>> listeners = new ConcurrentHashMap<>(5);

    /**
     * 添加事件
     *
     * @param listener 要添加的事件
     */
    public static synchronized void addListener(ApplicationListener listener) {
        Class<?> aclass = Reflection.getInterfaceT(listener.getClass(), 1);
        CopyOnWriteArraySet<ApplicationListener> listenerSet = new CopyOnWriteArraySet<>();
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
        CopyOnWriteArraySet<ApplicationListener> listenerSet = new CopyOnWriteArraySet<>();
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
        CopyOnWriteArraySet<ApplicationListener> listenerSet = listeners.get(aclass);
        notifyListeners(listenerSet, event);
    }

    /**
     * 通知所有的Listener
     */
    @SuppressWarnings("all")
    private static void notifyListeners(CopyOnWriteArraySet<ApplicationListener> listenerSet, ApplicationEvent event) {
        listenerSet.forEach(listener -> listener.onApplicationEvent(event));
    }
}
