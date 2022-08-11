package top.liumian.zipkin.agent.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author liumian  2022/8/11 08:43
 */
public interface InstanceMethodsInterceptPoint {

    /**
     * class instance methods matcher.
     *
     * @return methods matcher
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * @return represents a class name, the class instance must instanceof InstanceMethodsAroundInterceptor.
     */
    String getMethodsInterceptor();

    boolean isOverrideArgs();


}
