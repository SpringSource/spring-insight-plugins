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
package com.springsource.insight.plugin.jdbc;

import com.springsource.insight.collection.test.OperationCollectionAspectTestSupport;
import com.springsource.insight.intercept.operation.Operation;

/**
 * 
 */
public abstract class JdbcStatementOperationCollectionTestSupport extends OperationCollectionAspectTestSupport {
	protected JdbcStatementOperationCollectionTestSupport () {
		super();
	}
    
    protected Operation assertJdbcOperation (String sql) {
        return assertJdbcOperation(getLastEntered(), sql);
    }
    
    protected Operation assertJdbcOperation (Operation operation, String sql) {
        assertNotNull("No operation collected", operation);
        assertEquals("Mismatched type", JdbcOperationExternalResourceAnalyzer.TYPE, operation.getType());
        assertEquals("Mismatched SQL statement", sql, operation.get("sql", String.class));
        return operation;
    }
}
