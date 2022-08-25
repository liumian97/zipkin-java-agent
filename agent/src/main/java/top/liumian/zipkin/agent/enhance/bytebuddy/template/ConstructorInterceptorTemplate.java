package top.liumian.zipkin.agent.enhance.bytebuddy.template;

import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/25 23:27
 */
public class ConstructorInterceptorTemplate {

    private static String tracingInterceptorClass;

    private static TracingInterceptor tracingInterceptor;

    @RuntimeType
    public Object intercept(@This EnhancedInstance enhancedInstance, @AllArguments Object[] allArguments,
                            @SuperCall Callable<?> callable, @Origin Method method) throws Throwable {

        prepare();
        return tracingInterceptor.invokeMethod(enhancedInstance, allArguments, callable, method);
    }

    private void prepare() {
        if (tracingInterceptor == null) {
            try {
                System.out.println("加载tracingInterceptor：" + tracingInterceptorClass);
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                tracingInterceptor = (TracingInterceptor) Class.forName(tracingInterceptorClass, true, loader).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("load " + tracingInterceptorClass + " instance failed");
            }
        }
    }

}
