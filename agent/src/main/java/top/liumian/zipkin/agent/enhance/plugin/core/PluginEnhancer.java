package top.liumian.zipkin.agent.enhance.plugin.core;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.bytebuddy.template.MethodInterceptorTemplate;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;
import top.liumian.zipkin.agent.enhance.plugin.define.PluginEnhanceDefine;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance.CONTEXT_ATTR_NAME;

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


        /**
         * Manipulate class source code.<br/>
         *
         * new class need:<br/>
         * 1.Add field, name {@link #CONTEXT_ATTR_NAME}.
         * 2.Add a field accessor for this field.
         *
         * And make sure the source codes manipulation only occurs once.
         *
         */
        if (!typeDescription.isAssignableTo(EnhancedInstance.class)) {
            newClassBuilder = newClassBuilder.defineField(
                            CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                    .implement(EnhancedInstance.class)
                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
        }

        // TODO: 2022/8/26 处理constructorInterceptor

        for (InstanceMethodsInterceptPoint interceptPoint : pluginEnhanceDefine.getInstanceMethodsInterceptPoints()) {
            ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(interceptPoint.getMethodsMatcher());
            if (pluginEnhanceDefine.isBootstrapClassPlugin()) {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(BootstrapPluginBooster.forInternalDelegateClass(interceptPoint.getMethodsInterceptor())));
            } else {
                newClassBuilder = newClassBuilder.method(junction)
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new MethodInterceptorTemplate(interceptPoint.getMethodsInterceptor(), classLoader)));
            }

        }
        return newClassBuilder;
    }


}
