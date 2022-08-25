package top.liumian.zipkin.plugin.jdk.executor;

import brave.Span;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractTracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/11 23:22
 */
public class ThreadPoolExecutorTracingInterceptor extends AbstractTracingInterceptor {


    @Override
    public Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        String traceName = method.getName();

        Object argument = allArguments[0];
        if (argument instanceof Runnable){
            return TracingUtil.injectTraceInfo(AbstractTracingInterceptor.TRACING, traceName, traceInfo -> {
                allArguments[0] = AbstractTracingInterceptor.TRACING.currentTraceContext().wrap((Runnable) allArguments[0]);
            });
        } else if (argument instanceof Callable){
            return TracingUtil.injectTraceInfo(AbstractTracingInterceptor.TRACING, traceName, traceInfo -> {
                allArguments[0] = AbstractTracingInterceptor.TRACING.currentTraceContext().wrap((Callable<?>) allArguments[0]);
            });
        } else {
            //do nothing
            return null;
        }
    }
}
