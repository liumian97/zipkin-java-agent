package top.liumian.zipkin.plugin.jdk.executor;

import brave.Span;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractInstanceTracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingResult;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/11 23:22
 */
public class ThreadPoolExecutorInterceptor extends AbstractInstanceTracingInterceptor {

    @Override
    public TracingResult beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        String traceName = method.getName();

        Object argument = allArguments[0];
        if (argument instanceof Runnable) {
            Span span = TracingUtil.injectTraceInfo(AbstractInstanceTracingInterceptor.TRACING, traceName, traceInfo -> {
                allArguments[0] = AbstractInstanceTracingInterceptor.TRACING.currentTraceContext().wrap((Runnable) allArguments[0]);
            });
            return new TracingResult(true, span);
        } else if (argument instanceof Callable) {
            Span span = TracingUtil.injectTraceInfo(AbstractInstanceTracingInterceptor.TRACING, traceName, traceInfo -> {
                allArguments[0] = AbstractInstanceTracingInterceptor.TRACING.currentTraceContext().wrap((Callable<?>) allArguments[0]);
            });
            return new TracingResult(true, span);

        } else {
            //do nothing
            return new TracingResult(false, null);
        }
    }
}
