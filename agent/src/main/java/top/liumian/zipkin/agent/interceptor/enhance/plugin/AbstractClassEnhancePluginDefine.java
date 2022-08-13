package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

import java.util.List;

/**
 * @author liumian  2022/8/11 08:47
 */
public abstract class AbstractClassEnhancePluginDefine {

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


    /**
     * Main entrance of enhancing the class.
     *
     * @param typeDescription target class description.
     * @param builder         byte-buddy's builder to manipulate target class's bytecode.
     * @param classLoader     load the given transformClass
     * @return the new builder, or <code>null</code> if not be enhanced.
     */
//    public DynamicType.Builder<?> define(TypeDescription typeDescription, DynamicType.Builder<?> builder,
//                                         ClassLoader classLoader) throws PluginException {
//        String interceptorDefineClassName = this.getClass().getName();
//        String transformClassName = typeDescription.getTypeName();
//
//        LOGGER.debug("prepare to enhance class {} by {}.", transformClassName, interceptorDefineClassName);
//        WitnessFinder finder = WitnessFinder.INSTANCE;
//        /**
//         * find witness classes for enhance class
//         */
//        String[] witnessClasses = witnessClasses();
//        if (witnessClasses != null) {
//            for (String witnessClass : witnessClasses) {
//                if (!finder.exist(witnessClass, classLoader)) {
//                    LOGGER.warn("enhance class {} by plugin {} is not activated. Witness class {} does not exist.", transformClassName, interceptorDefineClassName, witnessClass);
//                    return null;
//                }
//            }
//        }
//        List<WitnessMethod> witnessMethods = witnessMethods();
//        if (!CollectionUtil.isEmpty(witnessMethods)) {
//            for (WitnessMethod witnessMethod : witnessMethods) {
//                if (!finder.exist(witnessMethod, classLoader)) {
//                    LOGGER.warn("enhance class {} by plugin {} is not activated. Witness method {} does not exist.", transformClassName, interceptorDefineClassName, witnessMethod);
//                    return null;
//                }
//            }
//        }
//
//        /**
//         * find origin class source code for interceptor
//         */
//        DynamicType.Builder<?> newClassBuilder = this.enhance(typeDescription, builder, classLoader, context);
//
//        context.initializationStageCompleted();
//        LOGGER.debug("enhance class {} by {} completely.", transformClassName, interceptorDefineClassName);
//
//        return newClassBuilder;
//    }




}
