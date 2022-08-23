package top.liumian.zipkin.plugin.sdk.rocketmq.v4;

import brave.Span;
import org.apache.rocketmq.common.message.Message;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractTracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/8/10 21:07
 */
public class ProducerTracingInterceptor extends AbstractTracingInterceptor {

    @Override
    public Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        String traceName = "MQ/SEND";
        Message message = (Message) allArguments[2];
        Span span = TracingUtil.injectTraceInfo(tracing, traceName, traceInfo -> traceInfo.forEach(message::putUserProperty));
        span.tag("mq.topic", message.getTopic());
        return span;
    }

}
