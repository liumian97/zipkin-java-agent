package top.liumian.zipkin.agent.enhance.plugin.define;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorInterceptPoint;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;

import java.util.HashSet;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 抽象插件增强定义类
 *
 * @author liumian  2022/8/11 08:47
 */
public abstract class AbstractClassPluginEnhanceDefine implements PluginEnhanceDefine {

    private final Set<String> enhancedClassSet = new HashSet<>();

    @Override
    public ConstructorInterceptPoint[] getConstructorInterceptorPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[0];
    }

    public boolean isBootstrapClassPlugin() {
        return false;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {

        if (typeDescription.isInterface()) {
            return false;
        }

        if (this.getEnhanceClass().equalsIgnoreCase(typeDescription.getTypeName())) {
            return true;
        }

        ElementMatcher.Junction<TypeDescription> elementMatcher = hasSuperType(named(this.getEnhanceClass()).and(isInterface()));
        boolean matches = elementMatcher.matches(typeDescription);
        if (matches) {
            //必须直接实现enhanceInterface才进行增强，避免有多层继承关系时进行重复增强
            for (TypeDescription.Generic anInterface : typeDescription.getInterfaces()) {
                String typeName = anInterface.getTypeName();
                if (typeName.equalsIgnoreCase(this.getEnhanceClass())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
