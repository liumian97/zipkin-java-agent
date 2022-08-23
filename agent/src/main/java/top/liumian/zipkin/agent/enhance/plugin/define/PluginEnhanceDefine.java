package top.liumian.zipkin.agent.enhance.plugin.define;

import net.bytebuddy.description.type.TypeDescription;

/**
 * @author liumian  2022/8/23 22:33
 */
public interface PluginEnhanceDefine {

    /**
     * Instance methods intercept point. See {@link InstanceMethodsInterceptPoint}
     *
     * @return collections of {@link InstanceMethodsInterceptPoint}
     */
    InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();


    /**
     * Define the class for filtering class.
     *
     * @return class path
     */
    String getEnhanceClass();


    boolean isBootstrapClassPlugin();


    boolean isMatch(TypeDescription typeDescription);
}
