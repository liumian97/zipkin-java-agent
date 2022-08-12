package top.liumian.zipkin.agent.interceptor.enhance.bytebuddy;

import brave.Tracing;
import top.liumian.zipkin.core.tracing.TracingUtil;

/**
 * 对runnable进行包装，使其具备链路跟踪能力
 *
 * @author liumian  2022/8/8 22:28
 */
public class TracingRunnable implements Runnable {

    private final Tracing tracing;

    private final Runnable runnable;

    public TracingRunnable(Tracing tracing, Runnable runnable) {
        this.tracing = tracing;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        TracingUtil.newChildTrace(tracing, "execRunnable", span -> {
            runnable.run();
        });
    }
}
