package top.liumian.zipkin.agent.interceptor;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import net.bytebuddy.implementation.bind.annotation.*;
import top.liumian.zipkin.agent.plugin.TracingPlugin;
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
public class MethodTracingInterceptor {

    private final static Logger logger = Logger.getLogger(MethodTracingInterceptor.class.getName());

    private final TracingPlugin tracingPlugin;

    protected final Tracing tracing;

    public MethodTracingInterceptor(TracingPlugin tracingPlugin) {
        this.tracingPlugin = tracingPlugin;
        tracing = TracingUtil.getTracing();
    }

    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperCall Callable<?> callable,
                            @Origin Method method) throws Throwable {

        Span span = null;
        try {
            span = tracingPlugin.beforeMethod(method, allArguments, method.getParameterTypes());
        } catch (Throwable throwable) {
            logger.log(Level.SEVERE, "tracing before method error", throwable);
        }

        Object result = null;
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            result = callable.call();
            return result;
        } catch (Throwable throwable) {
            if (span != null) {
                tracingPlugin.handleMethodException(method, allArguments, method.getParameterTypes(), span, throwable);
            }
            throw throwable;
        } finally {
            if (span != null) {
                tracingPlugin.afterMethod(method, allArguments, method.getParameterTypes(), span, result);
                span.finish();
            }
        }


    }

}
