package top.liumian.zipkin.plugin.sdk.dubbo.v27x.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassPluginEnhanceDefine;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author liumian  2022/9/18 16:59
 */
public class DubboPluginEnhanceDefine extends AbstractClassPluginEnhanceDefine {

    private static final String ENHANCE_CLASS = "org.apache.dubbo.monitor.support.MonitorFilter";

    @Override
    public String getEnhanceClass() {
        return ENHANCE_CLASS;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("invoke");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "top.liumian.zipkin.plugin.sdk.dubbo.v27x.DubboInterceptor";
                    }
                }
        };
    }
}
