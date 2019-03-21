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
package com.springsource.insight.plugin.jws;

import org.junit.Test;
import org.springframework.ws.server.endpoint.annotation.Endpoint;

import com.springsource.insight.collection.OperationCollectionAspectSupport;
import com.springsource.insight.collection.method.AnnotationDrivenMethodOperationCollectionAspect;
import com.springsource.insight.collection.test.OperationCollectionAspectTestSupport;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationType;

/**
 *
 */
public class EndpointMethodOperationCollectionAspectTest
        extends OperationCollectionAspectTestSupport {
    public EndpointMethodOperationCollectionAspectTest() {
        super();
    }

    @Test
    public void testEndpointCollection() {
        ExampleEndpoint endpoint = new ExampleEndpoint();
        endpoint.perform();

        Operation op = getLastEntered();
        assertNotNull("No operation captured", op);
        assertEquals("Mismatched operation type", OperationType.METHOD, op.getType());
        assertEquals("Mismatched captured lable", "ExampleEndpoint#perform", op.getLabel());
    }

    @Override
    public OperationCollectionAspectSupport getAspect() {
        return AnnotationDrivenMethodOperationCollectionAspect.aspectOf();
    }

    @Endpoint
    private static class ExampleEndpoint {
        ExampleEndpoint() { /* ignored */ }

        public void perform() { /* ignored */ }

    }
}
