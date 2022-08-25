package top.liumian.zipkin.agent.enhance.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 实例方法拦截点
 *
 * @author liumian  2022/8/11 08:43
 */
public interface InstanceMethodsInterceptPoint {

    /**
     * 实例方法匹配器
     *
     * @return methods matcher
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * 该方法所对应的拦截器
     * 该拦截器必须继承{@link top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptor}.
     *
     * @return 返回一个拦截器类名，
     */
    String getMethodsInterceptor();

}
