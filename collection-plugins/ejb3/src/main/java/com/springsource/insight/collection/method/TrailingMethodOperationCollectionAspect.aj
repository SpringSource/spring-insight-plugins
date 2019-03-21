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
package com.springsource.insight.collection.method;

import org.aspectj.lang.JoinPoint;

import com.springsource.insight.collection.DefaultOperationCollector;
import com.springsource.insight.collection.OperationCollector;
import com.springsource.insight.collection.TrailingAbstractOperationCollectionAspect;
import com.springsource.insight.intercept.operation.Operation;

/**
 * Performs a similar functionality like MethodOperationCollectionAspect, but
 * allows for the possibility that the generated {@link Operation} may be
 * eventually discarded 
 */
public abstract aspect TrailingMethodOperationCollectionAspect
        extends TrailingAbstractOperationCollectionAspect {
	protected final JoinPointFinalizer	finalizer;

    protected TrailingMethodOperationCollectionAspect() {
        this(JoinPointFinalizer.getJoinPointFinalizerInstance());
    }

    protected TrailingMethodOperationCollectionAspect(JoinPointFinalizer finalizerInstance) {
    	this(finalizerInstance, new DefaultOperationCollector());
    }

    protected TrailingMethodOperationCollectionAspect(OperationCollector collector) {
    	this(JoinPointFinalizer.getJoinPointFinalizerInstance(), collector);
    }

    protected TrailingMethodOperationCollectionAspect(JoinPointFinalizer finalizerInstance, OperationCollector collector) {
        super(collector);
        
        if ((finalizer=finalizerInstance) == null) {
        	throw new IllegalStateException("No finalizer instance provided");
        }
    }

    @Override
    protected Operation createOperation(JoinPoint jp) {
        return finalizer.registerWithSelf(new Operation(),  jp);
    }
}
