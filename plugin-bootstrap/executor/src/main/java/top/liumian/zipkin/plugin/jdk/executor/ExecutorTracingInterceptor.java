package top.liumian.zipkin.plugin.jdk.executor;

import brave.Span;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractTracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/8/11 23:22
 */
public class ExecutorTracingInterceptor extends AbstractTracingInterceptor {


    @Override
    public Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        String traceName = "SUMMIT RUNNABLE";

        return TracingUtil.injectTraceInfo(AbstractTracingInterceptor.tracing, traceName, traceInfo -> {
            TracingRunnable tracingRunnable = new TracingRunnable(AbstractTracingInterceptor.tracing, (Runnable) allArguments[0]);
            allArguments[0] = AbstractTracingInterceptor.tracing.currentTraceContext().wrap(tracingRunnable);
        });

    }
}
