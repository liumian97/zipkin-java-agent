package top.liumian.zipkin.plugin.jdk.thread;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractInstanceTracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingResult;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/8/25 23:16
 */
public class ThreadMethodInterceptor extends AbstractInstanceTracingInterceptor {
    @Override
    protected TracingResult beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        TraceContext invocationContext = (TraceContext) enhancedInstance.getZkDynamicField();
        if (invocationContext != null) {
//            TracingUtil.getTracing().tracer().newChild(invocationContext);
//            Span span = TracingUtil.getTracing().tracer().nextSpan();
//            Span span = TracingUtil.getTracing().tracer().newChild(invocationContext);
            Span span = TracingUtil.getTracing().tracer().toSpan(invocationContext);
            return new TracingResult(true, span);
        } else {
            if (TracingUtil.getTracing().tracer() != null) {
                //新起一个链路
                Tracer tracer = TRACING.tracer();
                Span span = tracer.newTrace().start();
                return new TracingResult(true,span);
            } else {
                return new TracingResult(false, null);
            }
        }

    }
}
