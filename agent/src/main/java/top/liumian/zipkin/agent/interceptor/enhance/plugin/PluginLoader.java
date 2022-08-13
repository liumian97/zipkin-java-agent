package top.liumian.zipkin.agent.interceptor.enhance.plugin;

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

    public static final List<PluginDefine> PLUGIN_DEFINE_LIST = new ArrayList<>();

    public static final List<AbstractClassEnhancePluginDefine> ENHANCE_PLUGIN_INSTANCE_LIST = new ArrayList<>();

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

                        AbstractClassEnhancePluginDefine plugin = (AbstractClassEnhancePluginDefine) Class.forName(pluginDefine.getDefineClass(), true, classLoader).newInstance();
                        ENHANCE_PLUGIN_INSTANCE_LIST.add(plugin);
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
