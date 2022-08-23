package top.liumian.zipkin.plugin.test.jdk.executor;

import org.junit.Assert;
import org.junit.Test;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liumian  2022/8/8 22:34
 */
public class TracingExecutorTest {


    @Test
    public void test() throws InterruptedException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> {
            try {
                System.out.printf("Executors %s - %s%n", Thread.currentThread().getName(), TracingUtil.getTraceId());
                Assert.assertNotNull(TracingUtil.getTraceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        executorService.execute(runnable);

        executorService.shutdown();
    }

}
