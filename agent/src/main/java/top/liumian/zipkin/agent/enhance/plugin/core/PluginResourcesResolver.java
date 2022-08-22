/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package top.liumian.zipkin.agent.enhance.plugin.core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use the current classloader to read all plugin define file. The file must be named 'skywalking-plugin.def'
 */
public class PluginResourcesResolver {

    private final static Logger logger = Logger.getLogger(PluginResourcesResolver.class.getName());


    public static List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<URL>();
        Enumeration<URL> urls;
        try {
            urls = PluginResourcesResolver.class.getClassLoader().getResources("zipkin-plugin.def");

            while (urls.hasMoreElements()) {
                URL pluginUrl = urls.nextElement();
                cfgUrlPaths.add(pluginUrl);
                logger.log(Level.INFO, "find zipkin plugin define in "+ pluginUrl);
            }

            return cfgUrlPaths;
        } catch (IOException e) {
            logger.log(Level.WARNING, "read resources failure.", e);
        }
        return cfgUrlPaths;
    }
}
