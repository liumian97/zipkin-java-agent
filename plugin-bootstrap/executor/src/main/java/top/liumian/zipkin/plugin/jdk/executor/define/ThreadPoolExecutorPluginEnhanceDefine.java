package top.liumian.zipkin.plugin.jdk.executor.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassPluginEnhanceDefine;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;

/**
 * @author liumian
 * @date 2022/8/12 1:51 PM
 **/
public class ThreadPoolExecutorPluginEnhanceDefine extends AbstractClassPluginEnhanceDefine {

    private final String TRACING_METHOD = "execute";

    private final String TRACING_INTERCEPTOR = "top.liumian.zipkin.plugin.jdk.executor.ThreadPoolExecutorInterceptor";

    private final String ENHANCE_CLASS = "java.util.concurrent.ThreadPoolExecutor";


    @Override
    public boolean isBootstrapClassPlugin() {
        return true;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named(TRACING_METHOD).and(ElementMatchers.takesArguments(1));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return TRACING_INTERCEPTOR;
                    }
                }
        };
    }

    @Override
    public String getEnhanceClass() {
        return ENHANCE_CLASS;
    }
}
