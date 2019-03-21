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

package com.springsource.insight.plugin.springweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.springsource.insight.idk.test.AbstractExposedIdkPuginsTestSupport;
import com.springsource.insight.plugin.springweb.controller.ControllerEndPointAnalyzer;
import com.springsource.insight.plugin.springweb.http.ClientHttpRequestExternalResourceAnalyzer;
import com.springsource.insight.plugin.springweb.http.ClientHttpRequestTraceErrorAnalyzer;
import com.springsource.insight.plugin.springweb.remoting.HttpInvokerRequestExecutorExternalResourceAnalyzer;
import com.springsource.insight.plugin.springweb.remoting.HttpInvokerRequestExecutorTraceErrorAnalyzer;
import com.springsource.insight.plugin.springweb.validation.ValidationEndPointAnalyzer;
import com.springsource.insight.plugin.springweb.validation.ValidationErrorsMetricsGenerator;

/**
 * Makes sure that the exposed singletons are indeed visible 
 */
@ContextConfiguration(locations={ "classpath:META-INF/insight-plugin-springweb.xml" })
public class ExposedSpringWebPluginSpringBeanTest
		extends AbstractExposedIdkPuginsTestSupport<SpringWebPluginRuntimeDescriptor> {
    /* NOTE: all beans are autowired with required=false since we want
     * to have specific tests for each and we don't want to fail ALL the
     * tests if one bean is missing
     */
	@Autowired(required=false) protected ControllerEndPointAnalyzer	controllerEndPointAnalyzer;
	@Autowired(required=false) protected ValidationEndPointAnalyzer	validationEndPointAnalyzer;
	@Autowired(required=false) protected ValidationErrorsMetricsGenerator	validationErrorsMetricsGenerator;
	@Autowired(required=false) protected SpringWebPluginRuntimeDescriptor	springWebPluginRuntimeDescriptor;
	@Autowired(required=false) protected HttpInvokerRequestExecutorExternalResourceAnalyzer httpInvokerRequestExecutorExternalResourceAnalyzer;
	@Autowired(required=false) protected HttpInvokerRequestExecutorTraceErrorAnalyzer	httpInvokerRequestExecutorTraceErrorAnalyzer;
	@Autowired(required=false) protected ClientHttpRequestTraceErrorAnalyzer	clientHttpRequestTraceErrorAnalyzer;
	@Autowired(required=false) protected ClientHttpRequestExternalResourceAnalyzer	clientHttpRequestExternalResourceAnalyzer;

	public ExposedSpringWebPluginSpringBeanTest() {
		super(SpringWebPluginRuntimeDescriptor.class);
	}
}
