/**
 * Copyright (c) 2009-2011 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.insight.plugin.neo4j;

import java.util.Collection;
import java.util.List;

import com.springsource.insight.intercept.plugin.PluginRuntimeDescriptor;
import com.springsource.insight.intercept.topology.ExternalResourceAnalyzer;
import com.springsource.insight.util.ArrayUtil;

public class Neo4JPluginRuntimeDescriptor extends PluginRuntimeDescriptor {
    public static final String PLUGIN_NAME = "spring-neo4j";
    private static final Neo4JPluginRuntimeDescriptor INSTANCE = new Neo4JPluginRuntimeDescriptor();
    private static final List<? extends ExternalResourceAnalyzer> extResAnalyzers =
            ArrayUtil.asUnmodifiableList(
                    Neo4JFindExternalResourceAnalyzer.getInstance(),
                    Neo4JQueryExternalResourceAnalyzer.getInstance(),
                    Neo4JLookupExternalResourceAnalyzer.getInstance(),
                    Neo4JTraverseExternalResourceAnalyzer.getInstance(),
                    Neo4JSaveExternalResourceAnalyzer.getInstance()
                    );

    private Neo4JPluginRuntimeDescriptor() {
        super();
    }

    public static final Neo4JPluginRuntimeDescriptor getInstance() {
        return INSTANCE;
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public Collection<? extends ExternalResourceAnalyzer> getExternalResourceAnalyzers() {
        return extResAnalyzers;
    }
}
