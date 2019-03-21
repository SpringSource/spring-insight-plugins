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
package com.springsource.insight.plugin.servlet;

import org.junit.Test;

import com.springsource.insight.collection.test.AbstractCollectionTestSupport;
import com.springsource.insight.intercept.application.ApplicationName;
import com.springsource.insight.intercept.endpoint.EndPointAnalysis;
import com.springsource.insight.intercept.endpoint.EndPointName;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationFields;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.SimpleFrameBuilder;
import com.springsource.insight.intercept.trace.Trace;
import com.springsource.insight.intercept.trace.TraceId;

public class ServletEndPointAnalyzerTest extends AbstractCollectionTestSupport {
    private final ApplicationName appKey = ApplicationName.valueOf("app");
    private final ServletEndPointAnalyzer analyzer = ServletEndPointAnalyzer.getInstance();
    
    public ServletEndPointAnalyzerTest () {
    	super();
    }

    @Test
    public void testLocateEndPoint() {
        Trace trace = createServletEndPointTrace();
        EndPointAnalysis analysis = analyzer.locateEndPoint(trace);
        assertEquals("Mismatched label", "Servlet: My stuff / servlet", analysis.getResourceLabel());
        assertEquals("Mismatched end point", EndPointName.valueOf("My stuff _ servlet"), analysis.getEndPointName());
        assertEquals("Mismatched example", "GET /path?fuu=bar", analysis.getExample());
    }
    
    @Test
    public void testLocateEndPointNoHttp() {
        Trace trace = createNonHttpTrace();
        EndPointAnalysis analysis = analyzer.locateEndPoint(trace);
        assertNull("Unexpected success: " + analysis, analysis);
    }
    
    private Trace createNonHttpTrace() {
        SimpleFrameBuilder builder = new SimpleFrameBuilder();
        builder.enter(new Operation());
        Frame topLevelFrame = builder.exit();
        return Trace.newInstance(appKey, TraceId.valueOf("0"), topLevelFrame);
        
    }
    private Trace createServletEndPointTrace() {
        SimpleFrameBuilder builder = new SimpleFrameBuilder();
        Operation httpOp = new Operation().type(OperationType.HTTP);
        httpOp.createMap("request")
            .put(OperationFields.URI, "/path?fuu=bar")
            .put("method", "GET")
            .put("servletName", "My stuff / servlet");
        builder.enter(httpOp);
        Frame httpFrame = builder.exit();
        return Trace.newInstance(appKey, TraceId.valueOf("0"), httpFrame);
    }

}
