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


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.springsource.insight.intercept.operation.Operation;

@ContextConfiguration("classpath:jdbc-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcStatementOperationCollectionAspectTest extends JdbcStatementOperationCollectionTestSupport {
    @Autowired
    private DataSource dataSource;

    public JdbcStatementOperationCollectionAspectTest() {
        super();
    }

    @Test
    public void testOperationCollection() throws SQLException {
        final String sql = "select * from appointment where owner = 'Agim' and dateTime = '2009-06-01'";
        Connection c = dataSource.getConnection();
        try {
            Statement ps = c.createStatement();
            try {
                ps.execute(sql);
            } finally {
                ps.close();
            }
        } finally {
            c.close();
        }

        Operation operation = assertJdbcOperation(sql);
        assertNull("Unexpected parameters", operation.get("params"));
    }

    @Override
    public JdbcStatementOperationCollectionAspect getAspect() {
        return JdbcStatementOperationCollectionAspect.aspectOf();
    }
}
