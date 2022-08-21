package top.liumian.zipkin.agent.interceptor.enhance.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.OverrideCallable;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.TracingInterceptor;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * 实例方法链路跟踪拦截器
 *
 * @author liumian  2022/8/10 20:28
 */
public class MethodWithOverrideArgsTracingInterceptorTemplate {

    private final static Logger logger = Logger.getLogger(MethodWithOverrideArgsTracingInterceptorTemplate.class.getName());

    private static String tracingInterceptorClass;

    private static TracingInterceptor tracingInterceptor;


    @RuntimeType
    public static Object intercept(@Morph OverrideCallable callable, @Origin Method method, @AllArguments Object[] allArguments) throws Throwable {
        System.out.println("执行方法123：" + method.getName());
        System.out.println("tracingInterceptorClass:" + tracingInterceptorClass);
        prepera();
        return tracingInterceptor.invokeMethod(allArguments, callable, method);
    }

//    @Advice.OnMethodEnter
//    @RuntimeType
//    public static void intercept(@Advice.Origin Method method, @Advice.AllArguments Object[] allArguments) throws Throwable {
//        System.out.println("执行方法123：" + method.getName());
//        System.out.println("tracingInterceptorClass:"+tracingInterceptorClass);
//        prepera();
//        return tracingInterceptor.invokeMethod(allArguments, callable, method);
//    }

//    @Advice.OnMethodExit
//    public static void exit(@Advice.Origin Method method, @Advice.AllArguments Object[] allArguments) throws Throwable {
//        System.out.println("方法退出：" + method.getName());
//    }

    private static void prepera() {
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
