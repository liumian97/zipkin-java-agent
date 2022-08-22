package top.liumian.zipkin.core.tracing;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static brave.handler.SpanHandler.NOOP;

/**
 * @author liumian  2022/8/10 20:29
 */
public class TracingUtil {

    private final static Tracing TRACING;

    static {
        TRACING = Tracing.newBuilder().localServiceName("tracingTest")
                .addSpanHandler(NOOP)
//                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(MDCScopeDecorator.get()).build())
                .build();
    }

    public static Tracing getTracing() {
        return TRACING;
    }

    public static String getTraceId(){
        Tracer tracer = TRACING.tracer();
        if (tracer.currentSpan() != null){
            return tracer.currentSpan().context().traceIdString();
        } else {
            return null;
        }
    }

    public static String getSpanId(){
        Tracer tracer = TRACING.tracer();
        if (tracer != null){
            return tracer.currentSpan().context().spanIdString();
        } else {
            return null;
        }
    }

    /**
     * 开启一个新的链路，适用于需要返回业务逻辑执行结果的场景
     *
     * @param tracing    tracing
     * @param tranceName 链路名称
     * @param function   自定义业务逻辑
     * @param <R>        返回类型
     * @return 业务逻辑执行结果
     */
    public static <R> R newTrace(Tracing tracing, String tranceName, Function<Span, R> function) {
        Tracer tracer = tracing.tracer();
        Span span = tracer.newTrace().name(tranceName).start();
        span.annotate(tranceName + ".start");
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            return function.apply(span);
        } catch (RuntimeException e) {
            span.error(e);
            throw e;
        } finally {
            span.tag("thread", Thread.currentThread().getName());
            span.annotate(tranceName + ".finish");
            span.finish();
        }
    }

    /**
     * 开启一个新的链路，适用于不需要返回业务逻辑执行结果的场景
     *
     * @param tracing    tracing
     * @param tranceName 链路名称
     * @param consumer   自定义业务逻辑
     */
    public static void newTrace(Tracing tracing, String tranceName, Consumer<Span> consumer) {
        Tracer tracer = tracing.tracer();
        Span span = tracer.newTrace().name(tranceName).start();
        span.annotate(tranceName + ".start");
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            consumer.accept(span);
        } catch (RuntimeException e) {
            span.error(e);
            throw e;
        } finally {
            span.tag("thread", Thread.currentThread().getName());
            span.annotate(tranceName + ".finish");
            span.finish();
        }
    }


    /**
     * 开启一条子链路，适用于需要返回业务逻辑执行结果的场景
     *
     * @param tracing   tracing
     * @param traceName 链路名称
     * @param function  自定义业务逻辑
     * @param <R>       返回类型
     * @return 业务逻辑返回结果
     */
    public static <R> R newChildTrace(Tracing tracing, String traceName, Function<Span, R> function) {
        Tracer tracer = tracing.tracer();
        Span span = tracer.nextSpan().name(traceName).start();
        span.annotate(traceName + ".start");
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            return function.apply(span);
        } catch (RuntimeException e) {
            span.error(e);
            throw e;
        } finally {
            span.tag("thread", Thread.currentThread().getName());
            span.annotate(traceName + ".finish");
            span.finish();
        }
    }


    /**
     * 开启一条子链路，适用于需要返回业务逻辑执行结果的场景
     *
     * @param tracing   tracing
     * @param traceName 链路名称
     * @param consumer  自定义业务逻辑
     */
    public static void newChildTrace(Tracing tracing, String traceName, Consumer<Span> consumer) {
        Tracer tracer = tracing.tracer();
        Span span = tracer.nextSpan().name(traceName).start();
        span.annotate(traceName + ".start");
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            consumer.accept(span);
        } catch (RuntimeException e) {
            span.error(e);
            throw e;
        } finally {
            span.tag("thread", Thread.currentThread().getName());
            span.annotate(traceName + ".finish");
            span.finish();
        }
    }


    /**
     * 将properties中的链路上下文提取出来并注入到内存中
     *
     * @param tracing   tracing
     * @param traceName 链路名称
     * @param supplier  定义链路上下文信息提取逻辑
     * @return 新开启的span
     */
    public static Span extractTraceInfo(Tracing tracing, String traceName, Supplier<Map<String, String>> supplier) {
        TraceContext.Extractor<Map<String, String>> extractor = tracing.propagation().extractor(Map::get);
        Tracer tracer = tracing.tracer();
        Span span;
        Map<String, String> traceInfo = supplier.get();
        if (traceInfo == null || traceInfo.isEmpty()) {
            span = tracer.newTrace().name(traceName).start();
        } else {
            TraceContextOrSamplingFlags traceContext = extractor.extract(traceInfo);
            if (traceContext != null && traceContext.context() != null) {
                span = tracer.newChild(traceContext.context()).name(traceName).start();
            } else {
                span = tracer.newTrace().name(traceName).start();
            }
        }
        span.kind(Span.Kind.CONSUMER);
        return span;
    }

    /**
     * 将当前链路上下文注入到properties中并开启一个span
     *
     * @param tracing   tracing
     * @param traceName 链路名称
     * @param consumer  将当前链路上下文注入到参数中
     * @return 新开启的span
     */
    public static Span injectTraceInfo(Tracing tracing, String traceName, Consumer<Map<String, String>> consumer) throws Throwable {
        Tracer tracer = tracing.tracer();
        Span span = tracer.nextSpan().name(traceName).start();
        TraceContext.Injector<Map<String, String>> injector = tracing.propagation().injector(Map::put);
        Map<String, String> traceInfo = new HashMap<>();
        injector.inject(span.context(), traceInfo);
        consumer.accept(traceInfo);
        span.kind(Span.Kind.PRODUCER);
        return span;
    }

}
