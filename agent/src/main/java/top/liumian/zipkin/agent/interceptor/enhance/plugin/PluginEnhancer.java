package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.interceptor.enhance.bytebuddy.MethodTracingInterceptorTemplate;
import top.liumian.zipkin.agent.interceptor.enhance.bytebuddy.MethodWithOverrideArgsTracingInterceptorTemplate;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author liumian
 * @date 2022/8/12 2:07 PM
 **/
public class PluginEnhancer {

    private final AbstractClassEnhancePluginDefine pluginDefine;

    public PluginEnhancer(AbstractClassEnhancePluginDefine pluginDefine) {
        this.pluginDefine = pluginDefine;
    }

    public DynamicType.Builder<?> enhance(TypeDescription typeDescription,
                                          DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) {

        for (InstanceMethodsInterceptPoint interceptPoint : pluginDefine.getInstanceMethodsInterceptPoints()) {
            ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(interceptPoint.getMethodsMatcher());
            if (pluginDefine.isBootstrapClassPlugin()) {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .withBinders(Morph.Binder.install(OverrideCallable.class))
                                .to(BootstrapPluginBoost.forInternalDelegateClass(interceptPoint.getMethodsInterceptor())));
//                                .to(new MethodWithOverrideArgsTracingInterceptorTemplate(interceptPoint.getMethodsInterceptor(), classLoader)));
            } else {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new MethodTracingInterceptorTemplate(interceptPoint.getMethodsInterceptor(), classLoader)));
            }

        }
        return newClassBuilder;
    }


}
