package top.liumian.zipkin.plugin.sdk.rocketmq.v4;

import brave.Span;
import org.apache.rocketmq.common.message.Message;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractInstanceTracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingResult;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/8/10 21:07
 */
public class ProducerInterceptor extends AbstractInstanceTracingInterceptor {

    @Override
    public TracingResult beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        String traceName = "MQ/SEND";
        Message message = (Message) allArguments[2];
        Span span = TracingUtil.injectTraceInfo(TRACING, traceName, traceInfo -> traceInfo.forEach(message::putUserProperty));
        span.tag("mq.topic", message.getTopic());
        return new TracingResult(true, span);
    }

}
