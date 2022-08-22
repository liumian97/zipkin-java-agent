package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/10 20:33
 */
public interface TracingInterceptor {


    /**
     * 对目标方法进行链路跟踪
     *
     * @param allArguments 被调用的所有参数
     * @param callable     被调用方法实例，byte buddy帮助封装到了callable#call方法中
     * @param method       被调用方法
     * @return 调用结果
     * @throws Throwable 异常
     */
    Object invokeMethod(Object[] allArguments, Callable<?> callable,
                        Method method) throws Throwable;

}
