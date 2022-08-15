package top.liumian.zipkin.plugin.test.executor;

import top.liumian.zipkin.core.tracing.TracingUtil;
import top.liumian.zipkin.plugin.jdk.executor.TracingExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liumian  2022/8/8 22:34
 */
public class TracingExecutorTest {


    public static void main(String[] args) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.printf("Executors %s - %s%n", Thread.currentThread().getName(),
                            TracingUtil.getTracing().tracer().currentSpan().context().traceIdString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        executorService.execute(runnable);

        new TracingExecutor().execute(runnable);

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }

}
