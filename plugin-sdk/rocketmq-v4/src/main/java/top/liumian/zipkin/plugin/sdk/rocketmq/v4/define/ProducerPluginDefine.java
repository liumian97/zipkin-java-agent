package top.liumian.zipkin.plugin.sdk.rocketmq.v4.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassEnhancePluginDefine;
import top.liumian.zipkin.agent.enhance.plugin.define.InstanceMethodsInterceptPoint;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author liumian  2022/8/11 09:18
 */
public class ProducerPluginDefine extends AbstractClassEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.apache.rocketmq.client.impl.MQClientAPIImpl";
    private static final String SEND_MESSAGE_METHOD_NAME = "sendMessage";
    private static final String ASYNC_METHOD_INTERCEPTOR = "top.liumian.zipkin.plugin.sdk.rocketmq.v4.ProducerTracingInterceptor";

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named(SEND_MESSAGE_METHOD_NAME).and(takesArguments(12));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return ASYNC_METHOD_INTERCEPTOR;
                    }
                }
        };
    }


    @Override
    public String getEnhanceClass() {
        return ENHANCE_CLASS;
    }
}
