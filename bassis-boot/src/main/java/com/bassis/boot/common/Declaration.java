package com.bassis.boot.common;

import java.io.File;

/**
 * 常量声明
 */
public interface Declaration {

    /**
     * 扫描起点常量声明
     */
    String scanRoot = "SCANROOT";

    /**
     * 编码
     */
    String encoding = "UTF-8";
    /**
     * 启动函数参数
     */
    String mainArgs = "mainArgs";
    /**
     * 启动模式-web
     */
    String startSchemaWeb = "web";

    /**
     * 启动模式-core
     */
    String startSchemaCore = "core";

    /**
     * 启动模式-rpc
     */
    String startSchemaRpc = "rpc";

    /**
     * 启动模式-All
     */
    String startSchemaAll = "all";

    /**
     * 配置文件名称
     */
    String config_file_name = "application.properties";

    /**
     * 配置文件文件夹
     */
    String resources_path = System.getProperty("user.dir") + File.separator + "resources";

    /**
     * 端口
     * bassis.server.port
     */
    String bassis_server_port = "bassis.server.port";

    /**
     * 启动名称
     * bassis.server.name
     */
    String bassis_server_name = "bassis.server.name";

    /**
     * 上下文前缀
     * bassis.context.path
     */
    String bassis_context_path = "bassis.context.path";

    /**
     * 请求后缀
     * bassis.url.pattern
     */
    String bassis_url_pattern = "bassis.url.pattern";

    /**
     * bean扫描起点
     * bassis.scan.root
     */
    String bassis_scan_root = "bassis.scan.root";

    /**
     * 启动模式
     * bassis.start.schema
     */
    String bassis_start_schema = "bassis.start.schema";

}
