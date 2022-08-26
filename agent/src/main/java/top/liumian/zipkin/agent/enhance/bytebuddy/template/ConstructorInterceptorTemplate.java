package top.liumian.zipkin.agent.enhance.bytebuddy.template;

import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptorInstanceLoader;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/25 23:27
 */
public class ConstructorInterceptorTemplate {

    private static TracingInterceptor tracingInterceptor;

    public ConstructorInterceptorTemplate(String interceptorClass, ClassLoader classLoader) {
        try {
            tracingInterceptor = TracingInterceptorInstanceLoader.load(interceptorClass, classLoader);
        } catch (Exception e) {
            throw new RuntimeException("load " + interceptorClass + " instance failed");
        }
    }

    @RuntimeType
    public Object intercept(@This EnhancedInstance enhancedInstance, @AllArguments Object[] allArguments,
                            @SuperCall Callable<?> callable, @Origin Method method) throws Throwable {

        return tracingInterceptor.invokeMethod(enhancedInstance, allArguments, callable, method);
    }

}
