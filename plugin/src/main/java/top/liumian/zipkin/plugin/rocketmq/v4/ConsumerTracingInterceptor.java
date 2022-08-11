package top.liumian.zipkin.plugin.rocketmq.v4;

import brave.Span;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.AbstractTracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

/**
 * @author liumian  2022/8/10 21:27
 */
public class ConsumerTracingInterceptor extends AbstractTracingInterceptor {

    @Override
    public Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        List<MessageExt> msgs = (List<MessageExt>) allArguments[0];
        if (msgs.size() > 1) {
            logger.log(Level.WARNING, "msgs size > 1, stop tracing");
            return null;
        } else {
            String traceName = "MQ/CONSUMER";
            return TracingUtil.extractTraceInfo(tracing, traceName, () -> {
                MessageExt messageExt = msgs.get(0);
                return messageExt.getProperties();
            });
        }
    }

    @Override
    public void afterMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable {
        if (result != null) {
            if (result instanceof ConsumeConcurrentlyStatus) {
                ConsumeConcurrentlyStatus status = (ConsumeConcurrentlyStatus) result;
                span.tag("consume.status", status.name());
            } else {
                ConsumeOrderlyStatus status = (ConsumeOrderlyStatus) result;
                span.tag("consume.status", status.name());
            }
        }
    }
}
