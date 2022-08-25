package top.liumian.zipkin.plugin.jdk.executor;

import brave.Tracing;
import top.liumian.zipkin.core.tracing.TracingUtil;

/**
 * 对runnable进行包装，使其具备链路跟踪能力
 *
 * @author liumian  2022/8/8 22:28
 */
public class TracingRunnable implements Runnable {

    private final Runnable runnable;

    public TracingRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        TracingUtil.newChildTrace("execRunnable", span -> {
            runnable.run();
        });
    }
}
