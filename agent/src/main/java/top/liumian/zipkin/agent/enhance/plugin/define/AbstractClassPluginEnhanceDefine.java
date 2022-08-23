package top.liumian.zipkin.agent.enhance.plugin.define;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author liumian  2022/8/11 08:47
 */
public abstract class AbstractClassPluginEnhanceDefine implements PluginEnhanceDefine {

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


    public boolean isBootstrapClassPlugin(){
        return false;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {

        if (typeDescription.isInterface()) {
            return false;
        }

        if(this.getEnhanceClass().equalsIgnoreCase(typeDescription.getTypeName())){
            return true;
        }

        ElementMatcher.Junction<TypeDescription> elementMatcher = hasSuperType(named(this.getEnhanceClass()));
        return elementMatcher.matches(typeDescription);
    }

    private void matchHierarchyClass(TypeDescription.Generic clazz, List<String> parentTypes) {
        parentTypes.remove(clazz.asRawType().getTypeName());
        if (parentTypes.size() == 0) {
            return;
        }

        for (TypeDescription.Generic generic : clazz.getInterfaces()) {
            matchHierarchyClass(generic, parentTypes);
        }

        TypeDescription.Generic superClazz = clazz.getSuperClass();
        if (superClazz != null && !clazz.getTypeName().equals("java.lang.Object")) {
            matchHierarchyClass(superClazz, parentTypes);
        }

    }

}
