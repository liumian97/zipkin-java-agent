package top.liumian.zipkin.agent.plugin;

import brave.Span;
import brave.Tracing;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * @author liumian  2022/8/10 20:32
 */
public abstract class AbstractTracingPlugin implements TracingPlugin {

    protected static final Logger logger = Logger.getLogger(AbstractTracingPlugin.class.getName());

    protected static final Tracing tracing = TracingUtil.getTracing();

    @Override
    public void afterMethod(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable {

    }

    @Override
    public void handleMethodException(Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Throwable throwable) {
        span.error(throwable);
    }
}
