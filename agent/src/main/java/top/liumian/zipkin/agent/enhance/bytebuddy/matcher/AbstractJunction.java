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

package top.liumian.zipkin.agent.enhance.bytebuddy.matcher;

import net.bytebuddy.matcher.ElementMatcher;
import top.liumian.zipkin.agent.enhance.plugin.core.PluginLoader;
import top.liumian.zipkin.agent.enhance.plugin.define.PluginEnhanceDefine;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJunction<V> implements ElementMatcher.Junction<V> {


    protected final Map<String, PluginEnhanceDefine> enhancePluginMap;

    public AbstractJunction() {
        enhancePluginMap = new HashMap<>();
        PluginLoader.PLUGIN_ENHANCE_DEFINE_LIST.forEach(enhancePluginDefine -> enhancePluginMap.put(enhancePluginDefine.getEnhanceClass(),enhancePluginDefine));
    }

    @Override
    public <U extends V> Junction<U> and(ElementMatcher<? super U> other) {
        return new Conjunction<U>(this, other);
    }

    @Override
    public <U extends V> Junction<U> or(ElementMatcher<? super U> other) {
        return new Disjunction<U>(this, other);
    }
}
