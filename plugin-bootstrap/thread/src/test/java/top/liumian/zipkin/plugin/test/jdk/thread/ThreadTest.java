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
    public void test() {

        System.out.println("hello world:" + Thread.currentThread().getName());
//        Runnable runnable = () -> System.out.println("Hello World: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
//        runnable.run();

        Runnable runnable = TracingUtil.newTrace("traceTest", span -> {
            System.out.println("new Trace: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
            Runnable runnable1 = () -> System.out.println("Hello World222: " + TracingUtil.getTraceId() + " - " + TracingUtil.getSpanId());
//            runnable1.run();
            return runnable1;
        });
        runnable.run();


    }

}
