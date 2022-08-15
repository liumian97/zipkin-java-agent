package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.interceptor.enhance.bytebuddy.MethodTracingInterceptorTemplate;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author liumian
 * @date 2022/8/12 2:07 PM
 **/
public class PluginEnhance {

    private final AbstractClassEnhancePluginDefine pluginDefine;

    public PluginEnhance(AbstractClassEnhancePluginDefine pluginDefine) {
        this.pluginDefine = pluginDefine;
    }

    public DynamicType.Builder<?> enhance(TypeDescription typeDescription,
                                          DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) {

        for (InstanceMethodsInterceptPoint interceptPoint : pluginDefine.getInstanceMethodsInterceptPoints()) {
            ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(interceptPoint.getMethodsMatcher());
            if (pluginDefine.isBootstrapClassPlugin()){
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(internalDelegate(interceptPoint.getMethodsInterceptor())));
            } else {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new MethodTracingInterceptorTemplate(interceptPoint.getMethodsInterceptor(), classLoader)));
            }

        }
        return newClassBuilder;
    }




    /**
     * Get the delegate class name.
     *
     * @param methodsInterceptor of original interceptor in the plugin
     * @return generated delegate class name
     */
    public static String internalDelegate(String methodsInterceptor) {
        return methodsInterceptor + "_internal";
    }

    /**
     * Load the delegate class from current class loader, mostly should be AppClassLoader.
     *
     * @param methodsInterceptor of original interceptor in the plugin
     * @return generated delegate class
     */
    public static Class forInternalDelegateClass(String methodsInterceptor) {
        try {
            return Class.forName(internalDelegate(methodsInterceptor));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
