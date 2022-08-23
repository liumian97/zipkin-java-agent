package top.liumian.zipkin.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import top.liumian.zipkin.agent.enhance.bytebuddy.matcher.AbstractJunction;
import top.liumian.zipkin.agent.enhance.plugin.core.BootstrapPluginBoost;
import top.liumian.zipkin.agent.enhance.plugin.core.PluginEnhancer;
import top.liumian.zipkin.agent.enhance.plugin.core.PluginLoader;
import top.liumian.zipkin.agent.enhance.plugin.define.PluginEnhanceDefine;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static top.liumian.zipkin.agent.enhance.plugin.core.PluginLoader.PLUGIN_ENHANCE_DEFINE_LIST;

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
        ElementMatcher.Junction<TypeDescription> judge = new AbstractJunction<TypeDescription>() {
            @Override
            public boolean matches(TypeDescription target) {
                for (PluginEnhanceDefine pluginEnhanceDefine : PLUGIN_ENHANCE_DEFINE_LIST) {
                    if (pluginEnhanceDefine.isMatch(target)){
                        return true;
                    }
                }
                return false;
            }
        };
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
            for (PluginEnhanceDefine pluginEnhanceDefine : PLUGIN_ENHANCE_DEFINE_LIST) {
                if (pluginEnhanceDefine.isMatch(typeDescription)){
                    PluginEnhancer pluginEnhance = new PluginEnhancer(pluginEnhanceDefine);
                    DynamicType.Builder<?> tmpNewBuilder = pluginEnhance.enhance(typeDescription, newBuilder, classLoader);
                    if (tmpNewBuilder != null) {
                        newBuilder = tmpNewBuilder;
                    }
                }
            }
            return newBuilder;
        }
    }




}
