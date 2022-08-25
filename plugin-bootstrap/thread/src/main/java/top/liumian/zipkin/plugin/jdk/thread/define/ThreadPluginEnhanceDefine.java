package top.liumian.zipkin.plugin.jdk.thread.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassPluginEnhanceDefine;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorInterceptPoint;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * @author liumian  2022/8/25 23:32
 */
public class ThreadPluginEnhanceDefine extends AbstractClassPluginEnhanceDefine {

    @Override
    public ConstructorInterceptPoint[] getConstructorInterceptorPoints() {
        return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return any();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return "top.liumian.zipkin.plugin.jdk.thread.ThreadConstructorInterceptor";
                    }
                }
        };
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named("run").and(ElementMatchers.takesArguments(0));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "top.liumian.zipkin.plugin.jdk.thread.ThreadMethodInterceptor";
                    }
                }
        };
    }

    @Override
    public boolean isBootstrapClassPlugin() {
        return true;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        return super.isMatch(typeDescription);
    }

    @Override
    public String getEnhanceClass() {
        return "java.lang.Runnable";
    }
}
