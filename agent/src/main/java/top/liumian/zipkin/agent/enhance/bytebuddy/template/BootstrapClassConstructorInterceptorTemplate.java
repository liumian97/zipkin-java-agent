package top.liumian.zipkin.agent.enhance.bytebuddy.template;

import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorTracingInterceptor;

/**
 * @author liumian  2022/8/25 23:27
 */
public class BootstrapClassConstructorInterceptorTemplate {


    private static String tracingInterceptorClass;

    private static ConstructorTracingInterceptor tracingInterceptor;


    @RuntimeType
    public static void intercept(@This EnhancedInstance enhancedInstance, @AllArguments Object[] allArguments) throws Throwable {

        System.out.println("线程：" + Thread.currentThread().getName());
        prepare();
        tracingInterceptor.onConstruct(enhancedInstance, allArguments, null);
    }

    private static void prepare() {
        if (tracingInterceptor == null) {
            try {
                System.out.println("加载tracingInterceptor：" + tracingInterceptorClass);
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                tracingInterceptor = (ConstructorTracingInterceptor) Class.forName(tracingInterceptorClass, true, loader).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("load " + tracingInterceptorClass + " instance failed");
            }
        }
    }

}
