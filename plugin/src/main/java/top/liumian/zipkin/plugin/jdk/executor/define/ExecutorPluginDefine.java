package top.liumian.zipkin.plugin.jdk.executor.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.AbstractClassEnhancePluginDefine;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.InstanceMethodsInterceptPoint;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author liumian
 * @date 2022/8/12 1:51 PM
 **/
public class ExecutorPluginDefine extends AbstractClassEnhancePluginDefine {

    private final String TRACING_METHOD = "execute";

    private final String TRACING_INTERCEPTOR = "top.liumian.zipkin.plugin.jdk.executor.ExecutorTracingInterceptor";

//    private final String ENHANCE_CLASS = "java.util.concurrent.Executor";
//    private final String ENHANCE_CLASS = "java.util.concurrent.ThreadPoolExecutor";
    private final String ENHANCE_CLASS = "top.liumian.zipkin.plugin.jdk.executor.TracingExecutor";


    @Override
    public boolean isBootstrapClassPlugin() {
        return false;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named(TRACING_METHOD).and(takesArguments(1));
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
