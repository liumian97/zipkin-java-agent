package top.liumian.zipkin.agent.interceptor.enhance.bytebuddy;

import brave.Span;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.AbstractTracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/8/11 23:22
 */
public class ExecutorTracingInterceptor extends AbstractTracingInterceptor {


    @Override
    public Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        System.out.println("before run");
        String traceName = "SUMMIT RUNNABLE";

        return TracingUtil.injectTraceInfo(tracing, traceName, traceInfo -> {
            TracingRunnable tracingRunnable = new TracingRunnable(tracing, (Runnable) allArguments[0]);
            allArguments[0] = tracing.currentTraceContext().wrap(tracingRunnable);
        });

    }
}
