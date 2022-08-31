
# 目标
打造zipkin最好用的Java SDK

# 目的
1. 降低zipkin Java SDK的使用门槛：原生zipkin Java SDK - brave 配置较多，需要开发者手动配置，对初学者门槛较高
2. 学习skywalking、java-agent、字节码生成等技术：zipkin-java-agent主要参考skywalking Java SDK实现，并对其进行了简化，是一个学习skywalking Java SDK原理不错的项目


# 架构

## 依赖技术

1. java-agent
2. byte-buddy
3. skywalking Java SDK思想

## 源码结构

### 模块
项目共有4个模块，分别为：
1. core：核心模块。负责Tracing的初始化以及封装公共Tracing API给开发者。
2. agent：负责字节码增强。对plugin模块进行配置文件扫描并加载PluginInterceptor，以及对目标类的拦截增强
3. plugin-bootstrap：负责对Java原生类进行增强。Java原生类通过Bootstrap Classloader进行加载，意味着相关的拦截器也需要被Bootstrap classloader加载才能生效
4. plugin-sdk：负责对第三方SDK进行增强。如RocketMQ、Redis等
5. 其中plugin-bootstrap与plugin-sdk两个模块中的子模块作为plugin的最小定义粒度，即每一个子模块都定义了一种链路跟踪plugin

### 包结构
在plugin-bootstrap和plugin-sdk两个模块中的子模块包结构为
 - `top.liumian.zipkin.plugin.{sdkorjdk}.{sdkname}`：基础的包路径
 - `top.liumian.zipkin.plugin.{sdkorjdk}.{sdkname}.define`：用于描述、定义plugin，如Interceptor、InterceptPoint
 - `resources/zipkin-plugin.def`：配置plugin define的位置，agent启动时将被扫描

## 流程图


## 架构图


# 使用手册

# 支持的链路跟踪插件

## Java原生类
1. ThreadPoolExecutor
2. Callable、Runnable

## 第三方框架
1. RocketMQ:V4.x

# 如何贡献