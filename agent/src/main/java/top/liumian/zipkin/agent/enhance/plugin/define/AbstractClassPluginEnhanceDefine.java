package top.liumian.zipkin.agent.enhance.plugin.define;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if (typeDescription.isInterface()){
            return false;
        }

        if(this.getEnhanceClass().equalsIgnoreCase(typeDescription.getTypeName())){
            return true;
        }

        List<String> parentTypes = new ArrayList<String>(Arrays.asList(getEnhanceClass()));

        TypeList.Generic implInterfaces = typeDescription.getInterfaces();
        for (TypeDescription.Generic implInterface : implInterfaces) {
            matchHierarchyClass(implInterface, parentTypes);
        }

        if (typeDescription.getSuperClass() != null) {
            matchHierarchyClass(typeDescription.getSuperClass(), parentTypes);
        }

        return parentTypes.size() == 0;

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
