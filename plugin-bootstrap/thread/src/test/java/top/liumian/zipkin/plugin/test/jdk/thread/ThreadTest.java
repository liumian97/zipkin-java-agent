package top.liumian.zipkin.plugin.test.jdk.thread;

import brave.Span;
import org.junit.Test;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.util.function.Consumer;

/**
 * @author liumian
 * @date 2022/8/26 7:24 PM
 **/
public class ThreadTest {


    @Test
    public void test() throws Exception {

        System.out.println("hello world:" + Thread.currentThread().getName());
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable1: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
            }
        };
        runnable1.run();

        Runnable runnable = TracingUtil.newChildTrace("traceTest", span -> {
            System.out.println("new Trace: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                    System.out.println("runnable2: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
                }
            };
            return runnable2;
        });
        runnable.run();


    }

}
