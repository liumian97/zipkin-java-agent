package top.liumian.zipkin.agent.enhance.plugin.interceptor;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author liumian  2022/8/10 20:32
 */
public abstract class AbstractInstanceTracingInterceptor implements TracingInterceptor {

    protected static final Logger logger = Logger.getLogger(AbstractInstanceTracingInterceptor.class.getName());

    protected static final Tracing TRACING = TracingUtil.getTracing();

    /**
     * 方法前置处理
     *
     * @param enhancedInstance 被增强的目标类
     * @param method           被拦截的方法
     * @param allArguments     所有的调用参数
     * @param argumentsTypes   参数所对应的类型
     * @return 链路跟踪结果
     * @throws Throwable 异常
     */
    protected abstract TracingResult beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable;


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
    protected void afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable {

    }

    @Override
    public Object invokeMethod(EnhancedInstance enhancedInstance, Object[] allArguments, Callable<?> callable, Method method) throws Throwable {
        TracingResult tracingResult;
        try {
            tracingResult = this.beforeMethod(enhancedInstance, method, allArguments, method.getParameterTypes());
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "tracing before method error", throwable);
            tracingResult = new TracingResult(false, null);
        }

        if (!tracingResult.isContinueTracing()) {
            return callable.call();
        }
        Span span = tracingResult.getSpan();
        Object result = null;
        try (Tracer.SpanInScope spanInScope = TRACING.tracer().withSpanInScope(span)) {
            result = callable.call();
            return result;
        } catch (Throwable throwable) {
            this.handleMethodException(enhancedInstance, method, allArguments, method.getParameterTypes(), span, throwable);
            throw throwable;
        } finally {
            this.afterMethod(enhancedInstance, method, allArguments, method.getParameterTypes(), span, result);
            span.finish();
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
    protected void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Throwable throwable) {
        span.error(throwable);
    }
}
