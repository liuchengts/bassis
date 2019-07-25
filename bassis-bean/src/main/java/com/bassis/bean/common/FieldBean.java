package com.bassis.bean.common;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 属性注入的bean
 */
public class FieldBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前需要注入的对象
     */
    private Object object;
    /**
     * 当前需要注入的对象属性
     */
    private Field field;
    /**
     * 当前需要注入的资源
     */
    private Class<?> fieldClass;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    public FieldBean(Object object, Field field, Class<?> fieldClass) {
        this.object = object;
        this.field = field;
        this.fieldClass = fieldClass;
    }

    public FieldBean() {
    }
}
