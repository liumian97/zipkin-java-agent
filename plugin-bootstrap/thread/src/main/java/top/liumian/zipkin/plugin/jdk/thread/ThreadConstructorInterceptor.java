package top.liumian.zipkin.plugin.jdk.thread;

import brave.Tracer;
import brave.propagation.TraceContext;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorTracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/25 23:16
 */
public class ThreadConstructorInterceptor implements ConstructorTracingInterceptor {

    @Override
    public void onConstruct(EnhancedInstance enhancedInstance, Object[] allArguments, Method method) throws Throwable {
        try{
            Tracer tracer = TracingUtil.getTracing().tracer();
            if (tracer != null){
                //开启了链路跟踪
                System.out.println("ThreadConstructorInterceptor: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
                final TraceContext invocationContext = TracingUtil.getTracing().currentTraceContext().get();
                enhancedInstance.setZkDynamicField(invocationContext);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
