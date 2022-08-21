package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author liumian  2022/8/10 20:32
 */
public abstract class AbstractTracingInterceptor implements TracingInterceptor {

    protected static final Logger logger = Logger.getLogger(AbstractTracingInterceptor.class.getName());

    protected static final Tracing tracing = TracingUtil.getTracing();

    @Override
    public void afterMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable {

    }

    @Override
    public Object invokeMethod(Object[] allArguments, OverrideCallable callable, Method method) throws Throwable {
        Span span = null;
        try {
            System.out.println("beforeMethod");
            span = this.beforeMethod(method, allArguments, method.getParameterTypes());
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "tracing before method error", throwable);
        }

        Object result = null;
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            System.out.println("call");
            result = callable.call(allArguments);
            return result;
        } catch (Throwable throwable) {
            if (span != null) {
                this.handleMethodException(method, allArguments, method.getParameterTypes(), span, throwable);
            }
            throw throwable;
        } finally {
            System.out.println("afterMethod");
            if (span != null) {
                this.afterMethod(method, allArguments, method.getParameterTypes(), span, result);
                span.finish();
            }
        }
    }

    @Override
    public void handleMethodException(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Throwable throwable) {
        span.error(throwable);
    }
}
