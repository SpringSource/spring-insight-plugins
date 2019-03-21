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

package com.springsource.insight.plugin.integration;

import java.util.List;

import org.junit.Test;

import com.springsource.insight.collection.test.AbstractCollectionTestSupport;
import com.springsource.insight.intercept.application.ApplicationName;
import com.springsource.insight.intercept.endpoint.EndPointAnalysis;
import com.springsource.insight.intercept.endpoint.EndPointName;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameBuilder;
import com.springsource.insight.intercept.trace.SimpleFrameBuilder;
import com.springsource.insight.intercept.trace.Trace;
import com.springsource.insight.intercept.trace.TraceId;

/**
 */
public class IntegrationEndPointAnalyzerTest extends AbstractCollectionTestSupport {
    private final ApplicationName app = ApplicationName.valueOf("app");
    private final IntegrationEndPointAnalyzer endPointAnalyzer = IntegrationEndPointAnalyzer.getInstance();
    private final List<OperationType> operationTypes = IntegrationEndPointAnalyzer.SI_OPS;

    public IntegrationEndPointAnalyzerTest() {
        super();
    }

    @Test
    public void locateChannelEndPoint() throws Exception {
        String beanName = "test";
        String beanType = "Channel";
        Operation operation = new Operation()
                .type(this.operationTypes.get(0))
                .label("MessageChannel#" + beanName)
                .put("siComponentType", beanType)
                .put("beanName", beanName)
                .put("payloadType", "java.lang.String")
                .put("idHeader", "123");

        FrameBuilder b = new SimpleFrameBuilder();
        b.enter(operation);
        b.enter(new Operation());
        Thread.sleep(100);
        b.exit();
        Frame integrationFrame = b.exit();
        Trace trace = Trace.newInstance(app, TraceId.valueOf("0"), integrationFrame);
        EndPointAnalysis endPoint = endPointAnalyzer.locateEndPoint(trace);
        assertEquals(EndPointName.valueOf("MessageChannel#test"), endPoint.getEndPointName());
        assertEquals(integrationFrame.getRange(), trace.getRange());
    }

    @Test
    public void locateHandlerEndPoint() throws Exception {
        String beanName = "test";
        String beanType = "MessageHandler";
        Operation operation = new Operation()
                .type(this.operationTypes.get(0))
                .label("MessageHandler#" + beanName)
                .put("siComponentType", beanType)
                .put("beanName", beanName)
                .put("payloadType", "java.lang.String")
                .put("idHeader", "123");

        FrameBuilder b = new SimpleFrameBuilder();
        b.enter(operation);
        b.enter(new Operation());
        Thread.sleep(100);
        b.exit();
        Frame integrationFrame = b.exit();
        Trace trace = Trace.newInstance(app, TraceId.valueOf("0"), integrationFrame);
        EndPointAnalysis endPoint = endPointAnalyzer.locateEndPoint(trace);
        assertEquals(EndPointName.valueOf("MessageHandler#test"), endPoint.getEndPointName());
        assertEquals(integrationFrame.getRange(), trace.getRange());
    }

}
