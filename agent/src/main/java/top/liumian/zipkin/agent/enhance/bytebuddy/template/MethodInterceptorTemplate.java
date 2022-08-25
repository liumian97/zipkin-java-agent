package top.liumian.zipkin.agent.enhance.bytebuddy.template;

import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptorInstanceLoader;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * 实例方法链路跟踪拦截器
 *
 * @author liumian  2022/8/10 20:28
 */
public class MethodInterceptorTemplate {

    private final static Logger logger = Logger.getLogger(MethodInterceptorTemplate.class.getName());

    private String tracingInterceptorClass;
    private TracingInterceptor tracingInterceptor;


    public MethodInterceptorTemplate(String interceptorClass, ClassLoader classLoader) {
        try {
            tracingInterceptor = TracingInterceptorInstanceLoader.load(interceptorClass, classLoader);
        } catch (Exception e) {
            throw new RuntimeException("load " + interceptorClass + " instance failed");
        }
    }

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
