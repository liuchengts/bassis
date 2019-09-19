bassis
---------------------------

### 目的在于开发出类似springboot使用方式的框架，同时加入其它特性，使其原生支持分布式，借此窥探java语言特性、jvm、spring实现方式，使技术广度和深度得到提升，对后续阅读理解spring、hibernate等开源框架有一个强大的基础，对jvm有一定的认知能力。

* 我是一个java开发者，而非spring开发者
* 技术为了形成系统，站到更高的层面而学习，而不是为了工作

### 说明

* bassis_tools 基于必须的jar支持重写一些工具方法
* bassis_bean  提供自动扫描、bean管理、全注解、ioc、aop、动态代理，全局事件等功能
* bassis_boot  嵌入tomcat8.5，增加main函数启动方式，需要bassis_bean的支持，实现框架的web，提供类似springmvc的一些功能，例如控制器自动匹配、拦截器栈、及松耦合方式使用bassis_hibernate完成多数据源自动注入与切换等功能
* bassis_data  前期基于jdbc进行开发

# 项目演示
### 演示 web 功能
* 直接运行 bassis-test 或 bassis-boot下的test部分 即可
### 演示 bean 功能(只启动框架核心的bean部分)
* 可以配置启动参数 bassis.start.schema=core 或者直接运行 bassis-bean下的test部分

## 注意

本框架为手写框架,基于jdk8开发与编译，在低于此版本的jre上运行可能会出现异常。
模块依赖关系为：
*  bassis_tools  基本第三方jar依赖
*  bassis_bean   bassis_tools 基础依赖
*  bassis_boot   bassis_bean 基础依赖
*  bassis_data   bassis_bean 基础依赖
*  详细情况请参见具体的pom.xml

## 用到的第三方jar包：

* log4j 1.2.17
* gson 2.8.0
* cglib 3.2.12
* mysql-connector-java 5.1.40
* servlet-api 4.0.0
* tomcat 8.5.35

## 目前进度(已完成)：
 
### bassis_tools
* 反射工具
* 基础、包装类型判定
* 自定义异常
* gc计数器
* json工具
* Properties文件读取器
* string工具
* 并发测试工具
* http请求工具
* log4j默认配置

### bassis_bean
* class扫描器
* @Autowired 实现
* @Component 实现
* @Aop 实现
* @Scope 实现
* 基于cglib与jdk动态代理
* 基于cglib的bean copy
* 自定义事件
* bean工厂
* 属性循环依赖注入
* 接口到实现类转换注入

### bassis_boot
* main函数启动tomcat
* 默认基本启动配置
* 默认filter及编码filter
* 默认servlet容器
* @Controller 实现
* 请求路径自动匹配bean实现
* @Interceptor及Interceptor栈实现
* @RequestMapping 实现
* 数据装配与解析返回基本实现

### bassis_data
* 封装jdbc

## 目前进度(需要调整及未完成的功能)：

### bassis_tools
* 无，按需求适当增加

### bassis_bean
* @Autowired 需要根据反射实现自动获取注入对象 去掉aclass参数 -- 已完成
* @Aop 需要根据反射实现自动获取注入对象 去掉aclass参数 -- 已完成
* @Scope 需要针对多实例模式下的bean做copy或者重新创建操作 -- 已完成

### bassis_boot
* 默认基本启动配置 需要支持个性化配置参数 -- 已完成
* 默认servlet容器 需要作为bassis_web基础依赖入口 -- 已完成
* @Controller 需要重写ioc逻辑 要与@Component保持一致 -- 已完成
* 请求路径自动匹配bean实现 需要优化路径存储已经寻址算法 -- 已完成
* @Interceptor及Interceptor栈实现 需要调试来兼容最新的aop功能
* @RequestMapping 实现 需要配合路径自动匹配 -- 已完成
* 数据装配与解析返回基本实现 需要重写定义大部分返回逻辑，抽离页面与数据的耦合 -- 已完成

### bassis_hibernate
* 多数据源jdbc
* 事务分组支持提交、回滚等功能
