package top.liumian.zipkin.agent.enhance.bytebuddy.template;

import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * bootstrap类实例方法链路跟踪拦截器
 *
 * @author liumian  2022/8/10 20:28
 */
public class BootstrapClassMethodInterceptorTemplate {

    private final static Logger logger = Logger.getLogger(BootstrapClassMethodInterceptorTemplate.class.getName());

    private static String tracingInterceptorClass;

    private static TracingInterceptor tracingInterceptor;


    @RuntimeType
    public static Object intercept(@This EnhancedInstance enhancedInstance, @SuperCall Callable<?> callable,
                                   @Origin Method method, @AllArguments Object[] allArguments) throws Throwable {
        System.out.println("执行方法：" + method.getName());
        prepare();
        return tracingInterceptor.invokeMethod(enhancedInstance, allArguments, callable, method);
    }

    private static void prepare() {
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
