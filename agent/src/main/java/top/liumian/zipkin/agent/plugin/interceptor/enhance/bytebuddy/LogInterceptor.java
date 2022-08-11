package top.liumian.zipkin.agent.plugin.interceptor.enhance.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liumian  2022/8/10 08:42
 */
public class LogInterceptor {

    @RuntimeType
    public static Object interceptor(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        System.err.println("执行方法：" + method.getName());
        // 其他。。。根据实际获取
        return callable.call();

    }
}
