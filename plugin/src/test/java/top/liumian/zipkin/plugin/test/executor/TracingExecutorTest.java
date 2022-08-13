package top.liumian.zipkin.plugin.test.executor;

import top.liumian.zipkin.core.tracing.TracingUtil;
import top.liumian.zipkin.plugin.jdk.executor.TracingExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author liumian  2022/8/8 22:34
 */
public class TracingExecutorTest {


    public static void main(String[] args) {

        Executors.newSingleThreadExecutor().submit( () ->
                System.out.printf("Executors %s - %s%n", Thread.currentThread().getName(),
                TracingUtil.getTracing().tracer().currentSpan().context().traceIdString())
        );

        new TracingExecutor().execute(() -> {
            System.out.printf("TracingExecutor %s - %s%n", Thread.currentThread().getName(),
                    TracingUtil.getTracing().tracer().currentSpan().context().traceIdString());
        });

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
