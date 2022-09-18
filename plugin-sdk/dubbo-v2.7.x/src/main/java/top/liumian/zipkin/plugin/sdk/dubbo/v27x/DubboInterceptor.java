package top.liumian.zipkin.plugin.sdk.dubbo.v27x;

import brave.Span;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.AbstractInstanceTracingInterceptor;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingResult;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author liumian  2022/9/18 16:02
 */
public class DubboInterceptor extends AbstractInstanceTracingInterceptor {


    @Override
    protected TracingResult beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {

        Invoker invoker = (Invoker) allArguments[0];
        RpcContext rpcContext = RpcContext.getContext();
        URL requestURL = invoker.getUrl();

        Span.Kind kind = rpcContext.isProviderSide() ? Span.Kind.SERVER : Span.Kind.CLIENT;
        Span span;
        Map<String, String> attachments = RpcContext.getContext().getAttachments();
        if (kind.equals(Span.Kind.CLIENT)) {
            final String traceName = "DUBBO/CLIENT_INVOKE";
            // When A service invoke B service, then B service then invoke C service, the parentId of the
            // C service span is A when read from invocation.getAttachments(). This is because
            // AbstractInvoker adds attachments via RpcContext.getContext(), not the invocation.
            // See org.apache.dubbo.rpc.protocol.AbstractInvoker(line 141) from v2.7.3

            span = TracingUtil.injectTraceInfo(TRACING, traceName, attachments::putAll);
        } else {
            String traceName = "DUBBO/SERVER_INVOKE";
            span = TracingUtil.extractTraceInfo(TRACING, traceName, () -> attachments);
        }
        span.tag("requestUrl",requestURL.toString());
        span.kind(kind);
        return new TracingResult(true,span);

    }
}
