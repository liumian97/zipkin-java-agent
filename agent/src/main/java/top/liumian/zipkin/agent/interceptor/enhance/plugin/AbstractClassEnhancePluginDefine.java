package top.liumian.zipkin.agent.interceptor.enhance.plugin;

/**
 * @author liumian  2022/8/11 08:47
 */
public abstract class AbstractClassEnhancePluginDefine {

    /**
     * Instance methods intercept point. See {@link InstanceMethodsInterceptPoint}
     *
     * @return collections of {@link InstanceMethodsInterceptPoint}
     */
    public abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();


    /**
     * Define the class for filtering class.
     *
     * @return class path
     */
    public abstract String getEnhanceClass();


}
