package top.liumian.zipkin.agent.enhance.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 构造方法拦截点
 *
 * @author liumian  2022/8/25 23:03
 */
public interface ConstructorInterceptPoint {

    /**
     * 构造方法匹配器
     *
     * @return 匹配器
     */
    ElementMatcher<MethodDescription> getConstructorMatcher();


    /**
     * 构造方法拦截器
     *
     * @return 拦截器
     */
    String getConstructorInterceptor();

}
