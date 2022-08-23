package top.liumian.zipkin.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import top.liumian.zipkin.agent.enhance.bytebuddy.matcher.AbstractJunction;
import top.liumian.zipkin.agent.enhance.plugin.define.AbstractClassEnhancePluginDefine;
import top.liumian.zipkin.agent.enhance.plugin.core.BootstrapPluginBoost;
import top.liumian.zipkin.agent.enhance.plugin.core.PluginEnhancer;
import top.liumian.zipkin.agent.enhance.plugin.core.PluginLoader;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static top.liumian.zipkin.agent.enhance.plugin.core.PluginLoader.ENHANCE_PLUGIN_INSTANCE_LIST;
import static top.liumian.zipkin.agent.enhance.plugin.core.PluginLoader.PLUGIN_DEFINE_LIST;

/**
 * @author liumian  2022/8/10 00:24
 */
public class ZipkinAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.err.println("premain start... ");
        System.err.println("args: " + agentArgs);

        PluginLoader.loadAllPlugin();


        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .ignore(nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(ElementMatchers.isSynthetic()));

        agentBuilder = BootstrapPluginBoost.inject(instrumentation, agentBuilder);

        agentBuilder.type(getTypeMatcher())
                .transform(new ZipkinTransform())
                .with(new AgentListener())
                .installOn(instrumentation);
    }

    private static ElementMatcher<? super TypeDescription> getTypeMatcher() {
        ElementMatcher.Junction judge = new AbstractJunction<NamedElement>() {
            @Override
            public boolean matches(NamedElement target) {
                return enhancePluginMap.containsKey(target.getActualName());
            }
        };
        judge = judge.and(not(isInterface()));
        for (int i = 0; i < ENHANCE_PLUGIN_INSTANCE_LIST.size(); i++) {
            AbstractClassEnhancePluginDefine pluginDefine = ENHANCE_PLUGIN_INSTANCE_LIST.get(i);
            judge = judge.or(hasSuperType(named(pluginDefine.getEnhanceClass())));
        }
        return judge;
    }


    private static class AgentListener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {

            System.out.println("On Transformation class:" + typeDescription.getName());

        }

        @Override
        public void onIgnored(final TypeDescription typeDescription,
                              final ClassLoader classLoader,
                              final JavaModule module,
                              final boolean loaded) {

        }

        @Override
        public void onError(final String typeName,
                            final ClassLoader classLoader,
                            final JavaModule module,
                            final boolean loaded,
                            final Throwable throwable) {
            System.out.println("Enhance class " + typeName + " error:" + throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }


    private static class ZipkinTransform implements AgentBuilder.Transformer {


        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                TypeDescription typeDescription,
                                                ClassLoader classLoader,
                                                JavaModule module,
                                                final ProtectionDomain protectionDomain) {


            DynamicType.Builder<?> newBuilder = builder;
            for (AbstractClassEnhancePluginDefine pluginDefine : ENHANCE_PLUGIN_INSTANCE_LIST) {
                if (pluginDefine.getEnhanceClass().equalsIgnoreCase(typeDescription.getTypeName()) || isMatch(typeDescription,pluginDefine.getEnhanceClass())){
                    PluginEnhancer pluginEnhance = new PluginEnhancer(pluginDefine);
                    DynamicType.Builder<?> tmpNewBuilder = pluginEnhance.enhance(typeDescription, newBuilder, classLoader);
                    if (tmpNewBuilder != null) {
                        newBuilder = tmpNewBuilder;
                    }
                }

            }
            return newBuilder;
        }
    }

    public static boolean isMatch(TypeDescription typeDescription,String enhansClass) {
        List<String> parentTypes = new ArrayList<String>(Arrays.asList(enhansClass));

        TypeList.Generic implInterfaces = typeDescription.getInterfaces();
        for (TypeDescription.Generic implInterface : implInterfaces) {
            matchHierarchyClass(implInterface, parentTypes);
        }

        if (typeDescription.getSuperClass() != null) {
            matchHierarchyClass(typeDescription.getSuperClass(), parentTypes);
        }

        return parentTypes.size() == 0;

    }

    private static void matchHierarchyClass(TypeDescription.Generic clazz, List<String> parentTypes) {
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
