package top.liumian.zipkin.plugin.sdk.httpclient.v3x;

import brave.Span;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractInstanceTracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingResult;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;

/**
 * @author liumian  2022/9/19 08:57
 */
public class HttpClientExecuteInterceptor extends AbstractInstanceTracingInterceptor {
    @Override
    protected TracingResult beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        final HttpMethod httpMethod = (HttpMethod) allArguments[1];
        if (httpMethod == null) {
            return new TracingResult(false,null);
        }
        final URI uri = httpMethod.getURI();
        final String requestURI = getRequestURI(uri);

        Span span = TracingUtil.injectTraceInfo(TRACING, requestURI, traceInfo -> traceInfo.forEach(httpMethod::addRequestHeader));
        span.tag("method",httpMethod.getName());
        span.kind(Span.Kind.CLIENT);
        return new TracingResult(true,span);

    }

    @Override
    protected void afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Object result) throws Throwable {
        if (result != null){
            final int statusCode = (Integer) result;
            span.tag("statusCode", String.valueOf(statusCode));
        }
    }

    @Override
    protected void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Span span, Throwable throwable) {
        span.error(throwable);
    }

    private String getRequestURI(URI uri) throws URIException {
        String requestPath = uri.getPath();
        return requestPath != null && requestPath.length() > 0 ? requestPath : "/";
    }
}
