package top.liumian.zipkin.agent.enhance.plugin.interceptor;

import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;

import java.lang.reflect.Method;

/**
 * 构造方法拦截器
 *
 * @author liumian  2022/8/25 23:24
 */
public interface ConstructorTracingInterceptor {

    /**
     * 对目标方法进行链路跟踪
     *
     * @param enhancedInstance 已经增强的目标实例
     * @param allArguments     被调用的所有参数
     * @param method           被调用方法
     * @return 调用结果
     * @throws Throwable 异常
     */
    void onConstruct(EnhancedInstance enhancedInstance, Object[] allArguments, Method method) throws Throwable;

}
