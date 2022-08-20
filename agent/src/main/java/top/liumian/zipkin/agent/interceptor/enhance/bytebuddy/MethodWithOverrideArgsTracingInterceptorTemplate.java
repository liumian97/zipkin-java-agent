package top.liumian.zipkin.agent.interceptor.enhance.bytebuddy;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.OverrideCallable;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.TracingInterceptor;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.logging.Level;
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

    protected static Tracing tracing;

    @RuntimeType
    public static Object intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph OverrideCallable callable,
                            @Origin Method method) throws Throwable {
        System.err.println("执行方法：" + method.getName());
        prepera();
        Span span = null;
        try {
            span = tracingInterceptor.beforeMethod(method, allArguments, method.getParameterTypes());
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "tracing before method error", throwable);
        }

        Object result = null;
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            result = callable.call(allArguments);
            return result;
        } catch (Throwable throwable) {
            if (span != null) {
                tracingInterceptor.handleMethodException(method, allArguments, method.getParameterTypes(), span, throwable);
            }
            throw throwable;
        } finally {
            if (span != null) {
                tracingInterceptor.afterMethod(method, allArguments, method.getParameterTypes(), span, result);
                span.finish();
            }
        }
    }

    public static void prepera(){
        if (tracingInterceptor == null){
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();

                tracingInterceptor = (TracingInterceptor) Class.forName(tracingInterceptorClass,true,loader).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("load " + tracingInterceptorClass + " instance failed");
            }
            tracing = TracingUtil.getTracing();
        }
    }


}
