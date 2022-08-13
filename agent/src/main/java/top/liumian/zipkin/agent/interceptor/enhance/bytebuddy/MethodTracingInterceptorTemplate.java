package top.liumian.zipkin.agent.interceptor.enhance.bytebuddy;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.TracingInterceptor;
import top.liumian.zipkin.agent.interceptor.enhance.plugin.TracingInterceptorInstanceLoader;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 实例方法链路跟踪拦截器
 *
 * @author liumian  2022/8/10 20:28
 */
public class MethodTracingInterceptorTemplate {

    private final static Logger logger = Logger.getLogger(MethodTracingInterceptorTemplate.class.getName());

    private final TracingInterceptor tracingInterceptor;

    protected final Tracing tracing;

    public MethodTracingInterceptorTemplate(String interceptorClass, ClassLoader classLoader) {
        try {
            tracingInterceptor = TracingInterceptorInstanceLoader.load(interceptorClass, classLoader);
        } catch (Exception e) {
            throw new RuntimeException("load " + interceptorClass + " instance failed");
        }
        tracing = TracingUtil.getTracing();
    }

    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperCall Callable<?> callable,
                            @Origin Method method) throws Throwable {
        System.err.println("执行方法：" + method.getName());
        Span span = null;
        try {
            span = tracingInterceptor.beforeMethod(method, allArguments, method.getParameterTypes());
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "tracing before method error", throwable);
        }

        Object result = null;
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            result = callable.call();
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

}
