package top.liumian.zipkin.plugin.jdk.executor;

import java.util.concurrent.Executor;

/**
 * @author liumian  2022/8/12 00:07
 */
public class TracingExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        System.out.println("execute");
        command.run();
    }
}
