package top.liumian.zipkin.agent.enhance.plugin.define;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.HashSet;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author liumian  2022/8/11 08:47
 */
public abstract class AbstractClassPluginEnhanceDefine implements PluginEnhanceDefine {

    private Set<String> enhancedClassSet = new HashSet<>();

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
