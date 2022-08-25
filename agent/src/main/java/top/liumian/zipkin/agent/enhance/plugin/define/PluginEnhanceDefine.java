package top.liumian.zipkin.agent.enhance.plugin.define;

import net.bytebuddy.description.type.TypeDescription;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorInterceptPoint;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;

/**
 * 插件增强定义类
 *
 * @author liumian  2022/8/23 22:33
 */
public interface PluginEnhanceDefine {

    /**
     * 获取所有的构造器拦截点
     *
     * @return 构造器拦截点列表
     */
    ConstructorInterceptPoint[] getConstructorInterceptorPoints();


    /**
     * 返回当前插件所有的实例方法拦截点
     * {@link InstanceMethodsInterceptPoint}
     *
     * @return collections of {@link InstanceMethodsInterceptPoint}
     */
    InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();


    /**
     * 目标增强类，可以为Class或者Interface
     *
     * @return class path
     */
    String getEnhanceClass();


    /**
     * 本插件是否需要被BootstrapClass Loader加载
     * 即enhanceClass是否为rt.jar中的class
     *
     * @return 是否需要被BootstrapClass Loader加载
     */
    boolean isBootstrapClassPlugin();


    /**
     * 当前{@link TypeDescription} 是否命中本插件
     *
     * @param typeDescription represent a Java type, i.e. a class or interface.
     * @return 是否命中
     */
    boolean isMatch(TypeDescription typeDescription);
}
