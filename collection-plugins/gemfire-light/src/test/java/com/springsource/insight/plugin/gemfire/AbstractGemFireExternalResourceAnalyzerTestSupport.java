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


package com.springsource.insight.plugin.gemfire;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.springsource.insight.collection.test.AbstractCollectionTestSupport;
import com.springsource.insight.intercept.application.ApplicationName;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.server.ServerName;
import com.springsource.insight.intercept.topology.ExternalResourceDescriptor;
import com.springsource.insight.intercept.topology.ExternalResourceType;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameId;
import com.springsource.insight.intercept.trace.SimpleFrame;
import com.springsource.insight.intercept.trace.Trace;
import com.springsource.insight.intercept.trace.TraceId;
import com.springsource.insight.util.time.TimeRange;

public abstract class AbstractGemFireExternalResourceAnalyzerTestSupport extends AbstractCollectionTestSupport  {
    protected final AbstractGemFireExternalResourceAnalyzer analyzer;
    
    protected AbstractGemFireExternalResourceAnalyzerTestSupport(AbstractGemFireExternalResourceAnalyzer analyzerInstance) {
        this.analyzer = analyzerInstance;
    }
    
	@Test
	public void testWithoutRemoteFrame() throws Exception {
		Operation op = new Operation();		
		op.type(analyzer.getOperationType());
		
		Frame frame = new SimpleFrame(FrameId.valueOf("0"),
				null,
				op,
				TimeRange.milliTimeRange(0, 1),
				Collections.<Frame>emptyList());

		Trace trace = new Trace(ServerName.valueOf("fake-server"),
				ApplicationName.valueOf("fake-app"),
				new Date(),
				TraceId.valueOf("fake-id"),
				frame);
		
		Collection<ExternalResourceDescriptor>    res=analyzer.locateExternalResourceName(trace);
		assertEquals("Mismatched number of results", 1, res.size());
		ExternalResourceDescriptor externalResourceDescriptor = res.iterator().next();

		assertEquals("Gemfire source frame", frame, externalResourceDescriptor.getFrame());
		assertNull("Gemfire external resource host", externalResourceDescriptor.getHost());
		assertEquals("Gemfire external resource port", -1, externalResourceDescriptor.getPort());
		assertEquals("Gemfire external resource type", ExternalResourceType.KVSTORE.name(), externalResourceDescriptor.getType());
		assertEquals("Gemfire external resource type", ExternalResourceType.KVSTORE.name(), externalResourceDescriptor.getType());
		assertEquals("Gemfire external resource name", AbstractGemFireExternalResourceAnalyzer.createName(null, -1), externalResourceDescriptor.getName());
		assertEquals("Gemfire external resource vendor", GemFireDefenitions.GEMFIRE, externalResourceDescriptor.getVendor());
		assertEquals("Gemfire external resource label", AbstractGemFireExternalResourceAnalyzer.createLabel(null, -1), externalResourceDescriptor.getLabel());
		assertEquals("Gemfire external incoming", Boolean.FALSE, Boolean.valueOf(externalResourceDescriptor.isIncoming()));
	}
    
    @Test
    public void testWithRemoteFrame() throws Exception {
        Operation op = new Operation();     
        op.type(analyzer.getOperationType());
        
        Frame remoteFrame = new SimpleFrame(FrameId.valueOf("1"),
                null,
                new Operation().type(GemFireDefenitions.TYPE_REMOTE.getType()),
                TimeRange.milliTimeRange(0, 1),
                Collections.<Frame>emptyList());
        
        List<Frame> frames = Collections.singletonList(remoteFrame);
        
        Frame frame = new SimpleFrame(FrameId.valueOf("0"),
                null,
                op,
                TimeRange.milliTimeRange(0, 1),
                frames);

        Trace trace = new Trace(ServerName.valueOf("fake-server"),
                ApplicationName.valueOf("fake-app"),
                new Date(),
                TraceId.valueOf("fake-id"),
                frame);
        
        Collection<ExternalResourceDescriptor>    res=analyzer.locateExternalResourceName(trace);
        assertEquals("Mismatched number of results", 0, res.size());
    }
}
