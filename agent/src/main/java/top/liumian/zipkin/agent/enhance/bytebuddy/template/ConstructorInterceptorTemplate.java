package top.liumian.zipkin.agent.enhance.bytebuddy.template;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorTracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptorInstanceLoader;

/**
 * @author liumian  2022/8/30 21:41
 */
public class ConstructorInterceptorTemplate {


    private ConstructorTracingInterceptor tracingInterceptor;

    public ConstructorInterceptorTemplate(String interceptorClass, ClassLoader classLoader) {
        try {
            tracingInterceptor = TracingInterceptorInstanceLoader.load(interceptorClass, classLoader);
        } catch (Exception e) {
            throw new RuntimeException("load " + interceptorClass + " instance failed");
        }
    }

    @RuntimeType
    public void intercept(@This EnhancedInstance enhancedInstance, @AllArguments Object[] allArguments) throws Throwable {

        System.out.println("线程：" + Thread.currentThread().getName());
        tracingInterceptor.onConstruct(enhancedInstance, allArguments, null);
    }

}
