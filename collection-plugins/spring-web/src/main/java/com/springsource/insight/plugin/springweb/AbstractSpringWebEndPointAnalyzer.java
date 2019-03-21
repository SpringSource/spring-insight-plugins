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

import com.springsource.insight.intercept.endpoint.AbstractSingleTypeEndpointAnalyzer;
import com.springsource.insight.intercept.endpoint.EndPointAnalysis;
import com.springsource.insight.intercept.endpoint.EndPointName;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.trace.Frame;

/**
 * 
 */
public abstract class AbstractSpringWebEndPointAnalyzer extends AbstractSingleTypeEndpointAnalyzer {
	protected AbstractSpringWebEndPointAnalyzer(OperationType type) {
		super(type);
	}

	@Override
	protected EndPointAnalysis makeEndPoint(Frame controllerFrame, int depth) {
        Operation controllerOp = controllerFrame.getOperation();
        String examplePath = EndPointAnalysis.getHttpExampleRequest(controllerFrame);
        EndPointName endPointName = EndPointName.valueOf(controllerOp);
        String endPointLabel = controllerOp.getLabel();

        return new EndPointAnalysis(endPointName, endPointLabel, examplePath, getOperationScore(controllerOp, depth), controllerOp);
    }
}
