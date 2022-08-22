package top.liumian.zipkin.agent.enhance.plugin.define;

/**
 * @author liumian
 * @date 2022/8/13 10:48 AM
 **/
public class PluginDefine {

    /**
     * Plugin name.
     */
    private final String name;

    /**
     * The class name of plugin defined.
     */
    private final String defineClass;

    private PluginDefine(String name, String defineClass) {
        this.name = name;
        this.defineClass = defineClass;
    }

    public static PluginDefine build(String define) {
        if (define == null || define.length() == 0) {
            throw new IllegalArgumentException(define);
        }

        String[] pluginDefine = define.split("=");
        if (pluginDefine.length != 2) {
            throw new IllegalArgumentException(define);
        }

        String pluginName = pluginDefine[0];
        String defineClass = pluginDefine[1];
        return new PluginDefine(pluginName, defineClass);
    }

    public String getDefineClass() {
        return defineClass;
    }

    public String getName() {
        return name;
    }

}
