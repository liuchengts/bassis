bassis
---------------------------
### 此分支已于2017年后停止更新，最新分支为 boot-1.0
### 写此框架的目的

* 从struts1到struts2，从spring到springmvc再到springboot及springcloud，发展历程短短几年时间，变化却翻天覆地，从设计模式到用法，甚至是软件设计思维的变化。由此引申出一个问题：框架基础的实现是什么？
多次求证感到迷茫，阅读框架源码源源不断，没有尽头，前人的思维不能尽看。
* 我问过好些java开发者，如果让他们不使用spring系，开发又当如何？
* 鉴于对springmvc粗有感想，借此基于自身理解着手写一套基础框架，目的在于轻量化框架，以及对mvc模式有更好的理解，借鉴springmvc的使用风格，揣测其可能的实现方式来重新实现。
* 奈何自身技术有限，空有想法，实现却不易，希望有志同道合的朋友不吝赐教，更好的学习、交流。在程序员80%的时间在写枯燥的业务代码之余，从其他代码中找到乐趣也是一件好事。

### 说明

* bassis_tools 基于必须的jar支持重写一些工具方法
* bassis_bean  bean管理工厂提供自动扫描、全注解、ioc等功能
* bassis_web  实现框架的web，提供类似springmvc的一些功能，例如控制器自动匹配、拦截器栈、aop、及松耦合方式使用bassis_hibernate完成多数据源自动注入与切换等功能
* bassis_hibernate  基于hibernate5.2进行封装，支持多数据源切换
* bassis_jdbc 后续会进行此项目开发

### 注意

本框架为手写框架,基于jdk8 目前尚未用到jdk8特性，但会对此框架进行长期的优化，尽量靠近jdk8的函数式编程。
依赖关系为：
*  bassis_tools  为bean 、web、hibernate（实际上并不依赖此tools） 基础依赖
*  bassis_web 依赖于servlet-api 2.5 以及bassis_bean
*  bassis_hibernate 依赖于hibernate-core 以及bassis_tools中的cglib、asm
详细情况请参见具体的pom.xml

### 用到的第三方jar包：

* log4j 1.2.17
* gson 2.8.0
* cglib 2.2
* asm 3.3.1
* hibernate-core 5.2.10.Final
* mysql-connector-java 5.1.40
* servlet-api 2.5



