package top.liumian.zipkin.agent.enhance.plugin.core;

import top.liumian.zipkin.agent.enhance.plugin.define.PluginDefine;
import top.liumian.zipkin.agent.enhance.plugin.define.PluginEnhanceDefine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author liumian
 * @date 2022/8/12 1:56 PM
 **/
public class PluginLoader {

    private static final Logger logger = Logger.getLogger(PluginLoader.class.getName());

    /**
     * 所有的plugin define
     * 即在定义在zipkin-plugin.def中的plugin
     */
    public static final List<PluginDefine> PLUGIN_DEFINE_LIST = new ArrayList<>();

    /**
     * 所有的plugin enhance define（插件增强定义）实例
     */
    public static final List<PluginEnhanceDefine> PLUGIN_ENHANCE_DEFINE_LIST = new ArrayList<>();

    /**
     * 扫描加载所有的插件
     */
    public static void loadAllPlugin() {

        ClassLoader classLoader = PluginLoader.class.getClassLoader();
        List<URL> resources = PluginResourcesResolver.getResources();
        for (URL resource : resources) {
            try (InputStream inputStream = resource.openStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        if (line.trim().length() == 0 || line.startsWith("#")) {
                            continue;
                        }
                        PluginDefine pluginDefine = PluginDefine.build(line);
                        PLUGIN_DEFINE_LIST.add(pluginDefine);

                        PluginEnhanceDefine plugin = (PluginEnhanceDefine) Class.forName(pluginDefine.getDefineClass(), true, classLoader).newInstance();
                        PLUGIN_ENHANCE_DEFINE_LIST.add(plugin);
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.SEVERE, "Failed to format plugin(" + line + ") define.");
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
