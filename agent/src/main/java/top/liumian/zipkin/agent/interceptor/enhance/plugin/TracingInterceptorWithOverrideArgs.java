package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import brave.Span;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/8/10 20:33
 */
public interface TracingInterceptorWithOverrideArgs {


    /**
     * 方法前置处理
     *
     * @param method         被拦截的方法
     * @param allArguments   所有的调用参数
     * @param argumentsTypes 参数所对应的类型
     * @return 当前链路的span
     * @throws Throwable 异常
     */
    Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable;


    /**
     * 方法后置处理
     *
     * @param method         被拦截的方法
     * @param allArguments   所有的调用参数
     * @param argumentsTypes 参数所对应的类型
     * @param span           当前链路的span
     * @param result         被拦截方法的返回结果
     * @throws Throwable 异常
     */
    void afterMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable;

    /**
     * 处理原始方法抛出来的异常
     *
     * @param method         被拦截的方法
     * @param allArguments   所有调用参数
     * @param argumentsTypes 参数所对应的类型
     * @param span           当前链路的span
     * @param throwable      捕获到的异常
     */
    void handleMethodException(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Throwable throwable);

}
