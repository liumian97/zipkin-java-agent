package top.liumian.zipkin.agent.interceptor.enhance.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liumian
 * @date 2022/8/12 2:21 PM
 **/
public class TracingInterceptorInstanceLoader {


    private static ConcurrentHashMap<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<String, Object>();
    private static ReentrantLock INSTANCE_LOAD_LOCK = new ReentrantLock();
    private static Map<ClassLoader, ClassLoader> EXTEND_PLUGIN_CLASSLOADERS = new HashMap<ClassLoader, ClassLoader>();

    /**
     * Load an instance of interceptor, and keep it singleton. Create {@link ClassLoader} for each
     * targetClassLoader, as an extend classloader. It can load interceptor classes from plugins, activations folders.
     *
     * @param className         the interceptor class, which is expected to be found
     * @param targetClassLoader the class loader for current application context
     * @param <T>               expected type
     * @return the type reference.
     */
    public static <T> T load(String className,
                             ClassLoader targetClassLoader) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (targetClassLoader == null) {
            targetClassLoader = TracingInterceptorInstanceLoader.class.getClassLoader();
        }
        String instanceKey = className + "_OF_" + targetClassLoader.getClass()
                .getName() + "@" + Integer.toHexString(targetClassLoader
                .hashCode());
        Object inst = INSTANCE_CACHE.get(instanceKey);
        if (inst == null) {
            inst = Class.forName(className, true, targetClassLoader).newInstance();
            if (inst != null) {
                INSTANCE_CACHE.put(instanceKey, inst);
            }
        }
        return (T) inst;
    }

}
