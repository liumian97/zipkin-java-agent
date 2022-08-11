package top.liumian.zipkin.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import top.liumian.zipkin.agent.interceptor.enhance.bytebuddy.MethodTracingInterceptorTemplate;

import java.lang.instrument.Instrumentation;

/**
 * @author liumian  2022/8/10 00:24
 */
public class ZipkinAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.err.println("premain start... ");
        System.err.println("args: " + agentArgs);
        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
                // method对所有方法进行拦截
                // intercept添加拦截器
                return builder.method(ElementMatchers.<MethodDescription>nameStartsWith("execute"))
                        .intercept(MethodDelegation.to(MethodTracingInterceptorTemplate.class));
            }
        };
        new AgentBuilder.Default()
                .type(getTypeMatcher())
                .transform(transformer)
                .with(new AgentListener())
                .installOn(instrumentation);
    }

    private static ElementMatcher<? super TypeDescription> getTypeMatcher() {
        return ElementMatchers.<TypeDescription>nameStartsWith("top.liumian.zipkin.plugin");
    }


    private static class AgentListener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {

            System.out.println("On Transformation class:" + typeDescription.getName());

        }

        @Override
        public void onIgnored(final TypeDescription typeDescription,
                              final ClassLoader classLoader,
                              final JavaModule module,
                              final boolean loaded) {

        }

        @Override
        public void onError(final String typeName,
                            final ClassLoader classLoader,
                            final JavaModule module,
                            final boolean loaded,
                            final Throwable throwable) {
            System.out.println("Enhance class " + typeName + " error:" + throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }


}
