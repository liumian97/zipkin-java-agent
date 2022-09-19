package top.liumian.zipkin.plugin.sdk.httpclient.v3x.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassPluginEnhanceDefine;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author liumian  2022/9/19 22:34
 */
public class HttpClientPluginEnhanceDefine extends AbstractClassPluginEnhanceDefine {

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("executeMethod").and(takesArguments(3))
                                .and(takesArgument(0, named("org.apache.commons.httpclient.HostConfiguration")))
                                .and(takesArgument(1, named("org.apache.commons.httpclient.HttpMethod")))
                                .and(takesArgument(2, named("org.apache.commons.httpclient.HttpState")));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "top.liumian.zipkin.plugin.sdk.httpclient.v3x.HttpClientExecuteInterceptor";
                    }
                }
        };
    }

    @Override
    public String getEnhanceClass() {
        return "org.apache.commons.httpclient.HttpClient";
    }
}
