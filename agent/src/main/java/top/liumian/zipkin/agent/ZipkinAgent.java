package top.liumian.zipkin.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import top.liumian.zipkin.agent.interceptor.LogInterceptor;

import java.lang.instrument.Instrumentation;

/**
 * @author liumian  2022/8/10 00:24
 */
public class ZipkinAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation){
        System.err.println("premain start... ");
        System.err.println("args: "+agentArgs);
        AgentBuilder.Transformer transformer=new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
                // method对所有方法进行拦截
                // intercept添加拦截器
                return builder.method(ElementMatchers.<MethodDescription>any())
                        .intercept(MethodDelegation.to(LogInterceptor.class));
            }
        };
        // 指定拦截org.pearl.order下
        new AgentBuilder.Default().type(ElementMatchers.<TypeDescription>nameStartsWith("top.liumian.zipkin.agent.test"))
                .transform(transformer).installOn(instrumentation);
    }


}
