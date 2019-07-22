package org.bassis.boot.common;

import java.io.Serializable;
import java.util.Set;

/**
 * 处理器的dto抽象类
 * 形状是一棵树
 */
public class ControllerDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 节点
     */
    String node;
    /**
     * 当前深度
     */
    Long depth;
    /**
     * 下级节点
     */
    Set<String> lowerNodes;
    /**
     * 父节点
     */
    String nodeParent;
    /**
     * 类实例
     */
    Class aClass;
    /**
     * 方法名
     */
    String methodName;
    /**
     * 方法参数类型集合
     */
    Set<Class> parameterTypes;
    /**
     * 方法返回类型
     */
    Class retClass;
}
