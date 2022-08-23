package top.liumian.zipkin.agent.enhance.plugin.interceptor;

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

    /**
     * 方法前置处理
     *
     * @param method         被拦截的方法
     * @param allArguments   所有的调用参数
     * @param argumentsTypes 参数所对应的类型
     * @return 当前链路的span
     * @throws Throwable 异常
     */
    protected abstract Span beforeMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable;


    /**
     * 方法后置处理
     *
     * @param method         被拦截的方法
     * @param allArguments   所有的调用参数
     * @param argumentsTypes 参数所对应的类型
     * @param span           当前链路的span
     * @param result         被拦截方法的返回结果
     * @throws Throwable 异常
     */
    protected void afterMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable {

    }

    @Override
    public Object invokeMethod(Object[] allArguments, Callable<?> callable, Method method) throws Throwable {
        Span span = null;
        try {
            span = this.beforeMethod(method, allArguments, method.getParameterTypes());
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "tracing before method error", throwable);
        }

        Object result = null;
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            result = callable.call();
            return result;
        } catch (Throwable throwable) {
            if (span != null) {
                this.handleMethodException(method, allArguments, method.getParameterTypes(), span, throwable);
            }
            throw throwable;
        } finally {
            if (span != null) {
                this.afterMethod(method, allArguments, method.getParameterTypes(), span, result);
                span.finish();
            }
        }
    }


    /**
     * 处理原始方法抛出来的异常
     *
     * @param method         被拦截的方法
     * @param allArguments   所有调用参数
     * @param argumentsTypes 参数所对应的类型
     * @param span           当前链路的span
     * @param throwable      捕获到的异常
     */
    protected void handleMethodException(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Throwable throwable) {
        span.error(throwable);
    }
}
