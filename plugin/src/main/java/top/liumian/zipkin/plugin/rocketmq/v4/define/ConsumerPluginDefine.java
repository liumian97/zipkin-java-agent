package top.liumian.zipkin.plugin.rocketmq.v4.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassEnhancePluginDefine;
import top.liumian.zipkin.agent.enhance.plugin.define.InstanceMethodsInterceptPoint;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author liumian  2022/8/11 09:31
 */
public class ConsumerPluginDefine extends AbstractClassEnhancePluginDefine {


    private static final String ENHANCE_CLASS = "org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently";
    private static final String CONSUMER_MESSAGE_METHOD = "consumeMessage";
    private static final String INTERCEPTOR_CLASS = "top.liumian.zipkin.plugin.rocketmq.v4.ConsumerTracingInterceptor";

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named(CONSUMER_MESSAGE_METHOD);
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }
                }
        };
    }

    @Override
    public String getEnhanceClass() {
        return ENHANCE_CLASS;
    }
}
