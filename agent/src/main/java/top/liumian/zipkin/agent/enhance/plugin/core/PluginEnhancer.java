package top.liumian.zipkin.agent.enhance.plugin.core;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.define.InstanceMethodsInterceptPoint;
import top.liumian.zipkin.agent.enhance.bytebuddy.template.MethodTracingInterceptorTemplate;
import top.liumian.zipkin.agent.enhance.plugin.define.PluginEnhanceDefine;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 插件增强器
 *
 * @author liumian
 * @date 2022/8/12 2:07 PM
 **/
public class PluginEnhancer {

    private final PluginEnhanceDefine pluginEnhanceDefine;

    public PluginEnhancer(PluginEnhanceDefine pluginEnhanceDefine) {
        this.pluginEnhanceDefine = pluginEnhanceDefine;
    }


    public DynamicType.Builder<?> enhance(TypeDescription typeDescription,
                                          DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) {

        for (InstanceMethodsInterceptPoint interceptPoint : pluginEnhanceDefine.getInstanceMethodsInterceptPoints()) {
            ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(interceptPoint.getMethodsMatcher());
            if (pluginEnhanceDefine.isBootstrapClassPlugin()) {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(BootstrapPluginBooster.forInternalDelegateClass(interceptPoint.getMethodsInterceptor())));
            } else {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new MethodTracingInterceptorTemplate(interceptPoint.getMethodsInterceptor(), classLoader)));
            }

        }
        return newClassBuilder;
    }


}
