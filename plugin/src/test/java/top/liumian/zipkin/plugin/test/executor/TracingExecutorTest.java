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

        Executor executor = new TracingExecutor();
        executor.execute(() -> {
            System.out.printf("%s - %s%n", Thread.currentThread().getName(),
                    TracingUtil.getTracing().tracer().currentSpan().context().traceIdString());
        });

    }

}
