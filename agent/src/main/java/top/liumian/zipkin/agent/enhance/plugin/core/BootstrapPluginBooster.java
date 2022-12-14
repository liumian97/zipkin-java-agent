package top.liumian.zipkin.agent.enhance.plugin.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.pool.TypePool;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorInterceptPoint;
import top.liumian.zipkin.agent.enhance.plugin.interceptor.InstanceMethodsInterceptPoint;
import top.liumian.zipkin.agent.enhance.plugin.define.PluginEnhanceDefine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * bootstrap启动器
 * 帮助加载所有需要被boostrap classloader加载的类进行加载
 *
 * @author liumian  2022/8/16 23:37
 */
public class BootstrapPluginBooster {


    private static String BOOTSTRAP_CLASS_METHOD_INTERCEPTOR_DELEGATE_TEMPLATE = "top.liumian.zipkin.agent.enhance.bytebuddy.template.BootstrapClassMethodInterceptorTemplate";
    private static String CONSTRUCTOR_INTERCEPTOR_DELEGATE_TEMPLATE = "top.liumian.zipkin.agent.enhance.bytebuddy.template.ConstructorInterceptorTemplate";


    /**
     * 高优先级的类
     */
    private static final String[] HIGH_PRIORITY_CLASSES = {
            "top.liumian.zipkin.agent.enhance.plugin.interceptor.TracingInterceptor",
            "top.liumian.zipkin.agent.enhance.plugin.interceptor.ConstructorTracingInterceptor",
            "top.liumian.zipkin.agent.enhance.plugin.core.EnhancedInstance",
    };


    /**
     * byte-buddy核心类
     */
    public static final String[] BYTE_BUDDY_CORE_CLASSES = {
            "net.bytebuddy.implementation.bind.annotation.RuntimeType",
            "net.bytebuddy.implementation.bind.annotation.This",
            "net.bytebuddy.implementation.bind.annotation.AllArguments",
            "net.bytebuddy.implementation.bind.annotation.AllArguments$Assignment",
            "net.bytebuddy.implementation.bind.annotation.SuperCall",
            "net.bytebuddy.implementation.bind.annotation.Origin",
            "net.bytebuddy.implementation.bind.annotation.Morph",
    };


    public static AgentBuilder inject(Instrumentation instrumentation, AgentBuilder agentBuilder) {
        Map<String, byte[]> classesTypeMap = new HashMap<>();

        if (!prepareJREPlugin(classesTypeMap)) {
            return agentBuilder;
        }

        for (String highPriorityClass : HIGH_PRIORITY_CLASSES) {
            loadHighPriorityClass(classesTypeMap, highPriorityClass);
        }


        /**
         * Prepare to open edge of necessary classes.
         */
        for (String highPriorityClass : BYTE_BUDDY_CORE_CLASSES) {
            loadHighPriorityClass(classesTypeMap, highPriorityClass);
        }

        /**
         * Inject the classes into bootstrap class loader by using Unsafe Strategy.
         * ByteBuddy adapts the sun.misc.Unsafe and jdk.internal.misc.Unsafe automatically.
         */
        ClassInjector.UsingUnsafe.Factory factory = ClassInjector.UsingUnsafe.Factory.resolve(instrumentation);
        factory.make(null, null).injectRaw(classesTypeMap);
        agentBuilder = agentBuilder.with(new AgentBuilder.InjectionStrategy.UsingUnsafe.OfFactory(factory));

        return agentBuilder;
    }


    /**
     * 检测是否需要有被bootstrap加载interceptor
     *
     * @param classesTypeMap 需要被bootstrap加载的字节码
     * @return
     */
    private static boolean prepareJREPlugin(Map<String, byte[]> classesTypeMap) {
        TypePool typePool = TypePool.Default.of(BootstrapPluginBooster.class.getClassLoader());
        List<PluginEnhanceDefine> enhancePluginInstanceList = PluginLoader.PLUGIN_ENHANCE_DEFINE_LIST;
        enhancePluginInstanceList.stream()
                .filter(PluginEnhanceDefine::isBootstrapClassPlugin)
                .filter(pluginDefine -> pluginDefine.getInstanceMethodsInterceptPoints().length > 0)
                .forEach(pluginDefine -> {
                    for (InstanceMethodsInterceptPoint interceptPoint : pluginDefine.getInstanceMethodsInterceptPoints()) {
                        generateDelegator(classesTypeMap, typePool, BOOTSTRAP_CLASS_METHOD_INTERCEPTOR_DELEGATE_TEMPLATE, interceptPoint.getMethodsInterceptor());
                    }

                    for (ConstructorInterceptPoint interceptPoint : pluginDefine.getConstructorInterceptorPoints()) {
                        generateDelegator(classesTypeMap, typePool, CONSTRUCTOR_INTERCEPTOR_DELEGATE_TEMPLATE, interceptPoint.getConstructorInterceptor());
                    }
                });
        return enhancePluginInstanceList.size() > 0;
    }

    /**
     * Generate the delegator class based on given template class. This is preparation stage level code generation.
     * <p>
     * One key step to avoid class confliction between AppClassLoader and BootstrapClassLoader
     *
     * @param classesTypeMap    hosts injected binary of generated class
     * @param typePool          to generate new class
     * @param templateClassName represents the class as template in this generation process. The templates are
     *                          pre-defined in SkyWalking agent core.
     */
    private static void generateDelegator(Map<String, byte[]> classesTypeMap, TypePool typePool,
                                          String templateClassName, String methodsInterceptor) {
        String internalInterceptorName = internalDelegate(methodsInterceptor);
        try {
            TypeDescription templateTypeDescription = typePool.describe(templateClassName).resolve();

            DynamicType.Unloaded interceptorType = new ByteBuddy()
                    .redefine(templateTypeDescription,
                            ClassFileLocator.ForClassLoader.of(PluginEnhancer.class.getClassLoader()))
                    .name(internalInterceptorName)
                    .field(named("tracingInterceptorClass"))
                    .value(methodsInterceptor)
                    .make();

            classesTypeMap.put(internalInterceptorName, interceptorType.getBytes());

//            InstrumentDebuggingClass.INSTANCE.log(interceptorType);
        } catch (Exception e) {
            throw new RuntimeException("Generate Dynamic plugin failure", e);
        }
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

    /**
     * The class loaded by this method means it only should be loaded once in Bootstrap classloader, when bootstrap
     * instrumentation active by any plugin
     *
     * @param loadedTypeMap hosts all injected class
     * @param className     to load
     */
    private static void loadHighPriorityClass(Map<String, byte[]> loadedTypeMap,
                                              String className) {
        byte[] enhancedInstanceClassFile;
        try {
            String classResourceName = className.replaceAll("\\.", "/") + ".class";
            InputStream resourceAsStream = BootstrapPluginBooster.class.getClassLoader().getResourceAsStream(classResourceName);

            if (resourceAsStream == null) {
                throw new RuntimeException("High priority class " + className + " not found.");
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            // read bytes from the input stream and store them in buffer
            while ((len = resourceAsStream.read(buffer)) != -1) {
                // write bytes from the buffer into output stream
                os.write(buffer, 0, len);
            }

            enhancedInstanceClassFile = os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        loadedTypeMap.put(className, enhancedInstanceClassFile);
    }

}
