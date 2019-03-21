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
package com.springsource.insight.plugin.apache.http.hc4;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.springsource.insight.collection.test.AbstractCollectionTestSupport;
import com.springsource.insight.intercept.application.ApplicationName;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationFields;
import com.springsource.insight.intercept.server.ServerName;
import com.springsource.insight.intercept.topology.ExternalResourceDescriptor;
import com.springsource.insight.intercept.topology.ExternalResourceType;
import com.springsource.insight.intercept.topology.MD5NameGenerator;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameId;
import com.springsource.insight.intercept.trace.SimpleFrame;
import com.springsource.insight.intercept.trace.Trace;
import com.springsource.insight.intercept.trace.TraceId;
import com.springsource.insight.util.ListUtil;
import com.springsource.insight.util.time.TimeRange;

/**
 *
 */
public class HttpExternalResourceAnalyzerTest extends AbstractCollectionTestSupport {
    private final HttpExternalResourceAnalyzer analyzer = HttpExternalResourceAnalyzer.getInstance();
    private static final AtomicLong frameIdGenerator = new AtomicLong(0L);

    public HttpExternalResourceAnalyzerTest() {
        super();
    }

    @Test
    public void testBasicAnalysis() throws URISyntaxException {
        final URI TEST_URI = new URI("http://test-host:3777/testBasicOperatiom");
        Trace trace = createTrace(createFrame(null, TEST_URI));
        Collection<ExternalResourceDescriptor> resList = analyzer.locateExternalResourceName(trace);
        assertEquals("Mismatched resources size", 1, resList.size());

        ExternalResourceDescriptor res = ListUtil.getFirstMember(resList);
        assertResourceContents(res, TEST_URI, "test-host:3777");
        assertSame("Mismatched frame instance", trace.getRootFrame(), res.getFrame());
    }

    @Test
    // make sure that repeated URI is reported only once
    public void testDuplicateURI() throws URISyntaxException {
        final URI TEST_URI = new URI("http://test-host:3777/testBasicOperatiom");
        Trace trace = createTrace(createFrame(createFrame(null, TEST_URI), TEST_URI));
        Collection<ExternalResourceDescriptor> resList = analyzer.locateExternalResourceName(trace);
        assertEquals("Mismatched resources size", 1, resList.size());
        assertResourceContents(ListUtil.getFirstMember(resList), TEST_URI, "test-host:3777");
    }

    @Test
    public void testMalformedURI() {
        Trace trace = createTrace(createFrame(null, "^^^this:is|a$bad(uri)"));
        Collection<ExternalResourceDescriptor> resList = analyzer.locateExternalResourceName(trace);
        assertEquals("Mismatched resources size", 0, ListUtil.size(resList));
    }

    static void assertResourceContents(ExternalResourceDescriptor res, URI uri, String label) {
        assertEquals("Mismatched name", MD5NameGenerator.getName(label), res.getName());
        assertEquals("Mismatched label", res.getLabel(), label);
        assertEquals("Mismatched type", ExternalResourceType.WEB_SERVER.name(), res.getType());
        assertEquals("Mismatched host", uri.getHost(), res.getHost());
        assertEquals("Mismatched port", HttpExternalResourceAnalyzer.resolvePort(uri), res.getPort());
        assertEquals("Mismatched direction", Boolean.FALSE, Boolean.valueOf(res.isIncoming()));
        assertNull("Mismatched vendor", res.getVendor());
    }

    static Frame createFrame(Frame parent, URI uri) {
        return createFrame(parent, uri.toString());
    }

    static Frame createFrame(Frame parent, String uri) {
        Operation op = new Operation().type(HttpClientDefinitions.TYPE);
        op.createMap("request").put(OperationFields.URI, uri);

        return new SimpleFrame(FrameId.valueOf(String.valueOf(frameIdGenerator.incrementAndGet())),
                parent,
                op,
                TimeRange.milliTimeRange(0, 1),
                Collections.<Frame>emptyList());
    }

    static Trace createTrace(Frame root) {
        return new Trace(ServerName.valueOf("fake-server"),
                ApplicationName.valueOf("fake-app"),
                new Date(System.currentTimeMillis()),
                TraceId.valueOf("fake-id"),
                root);
    }
}
