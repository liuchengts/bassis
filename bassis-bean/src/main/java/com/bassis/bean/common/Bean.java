package com.bassis.bean.common;

import java.io.Serializable;

/***
 * bean对象
 */
public class Bean implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 已经创建的实际对象
     */
    Object object;
    /**
     * 存储索引
     */
    Integer index;
    /**
     * 原始版本
     */
    long ov;
    /**
     * 当前版本
     */
    long cv;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
        addCv();
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        addCv();
    }

    public long getOv() {
        return ov;
    }

    public long getCv() {
        return cv;
    }

    public Bean(Object object, Integer index) {
        this.object = object;
        this.index = index;
        this.cv = this.ov = 1;
    }

    public Bean() {
    }

    private void addCv() {
        this.cv += 1;
    }

    /**
     * 版本是否有更改
     *
     * @return true表示没有更改 false表示更改了
     */
    public boolean isv() {
        return this.cv == this.ov;
    }
}
