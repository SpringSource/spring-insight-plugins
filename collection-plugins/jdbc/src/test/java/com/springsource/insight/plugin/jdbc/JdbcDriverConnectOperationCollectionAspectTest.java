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
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.springsource.insight.collection.ObscuredValueSetMarker;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationMap;
import com.springsource.insight.intercept.plugin.CollectionSettingsRegistry;
import com.springsource.insight.intercept.trace.ObscuredValueMarker;
import com.springsource.insight.util.StringUtil;

/**
 *
 */
public class JdbcDriverConnectOperationCollectionAspectTest
        extends JdbcConnectionOperationCollectionTestSupport {
    private ObscuredValueMarker originalMarker;
    private final ObscuredValueSetMarker replaceMarker = new ObscuredValueSetMarker();

    public JdbcDriverConnectOperationCollectionAspectTest() {
        super();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        JdbcDriverConnectOperationCollectionAspect aspectInstance = getAspect();
        originalMarker = aspectInstance.getSensitiveValueMarker();
        replaceMarker.clear();
        aspectInstance.setSensitiveValueMarker(replaceMarker);
    }

    @Override
    @After
    public void restore() {
        JdbcDriverConnectOperationCollectionAspect aspectInstance = getAspect();
        aspectInstance.setSensitiveValueMarker(originalMarker);
        // restore the original obfuscation settings
        CollectionSettingsRegistry registry = CollectionSettingsRegistry.getInstance();
        registry.set(JdbcDriverConnectOperationCollectionAspect.OBFUSCATED_PROPERTIES_SETTING,
                JdbcDriverConnectOperationCollectionAspect.DEFAULT_OBFUSCATED_PROPERTIES_LIST);
        super.restore();
    }

    @Test
    public void testDriverConnect() throws SQLException {
        Operation op = runConnectionTest();
        assertObscuredProperties(op,
                StringUtil.explode(JdbcDriverConnectOperationCollectionAspect.DEFAULT_OBFUSCATED_PROPERTIES_LIST, ","),
                true);
    }

    @Test
    public void testPropertiesObfuscation() throws SQLException {
        CollectionSettingsRegistry registry = CollectionSettingsRegistry.getInstance();
        // make sure the defaults are overridden
        registry.set(JdbcDriverConnectOperationCollectionAspect.OBFUSCATED_PROPERTIES_SETTING, "x,y,z");
        Operation op = runConnectionTest();
        assertObscuredProperties(op,
                StringUtil.explode(JdbcDriverConnectOperationCollectionAspect.DEFAULT_OBFUSCATED_PROPERTIES_LIST, ","),
                false);
    }

    @Override
    public JdbcDriverConnectOperationCollectionAspect getAspect() {
        return JdbcDriverConnectOperationCollectionAspect.aspectOf();
    }

    private Operation runConnectionTest() throws SQLException {
        Connection conn = connectDriver.connect(connectUrl, connectProps);
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            String connURL = metaData.getURL();
            /*
             * NOTE: this is not a test failure since this is not where we expect to
             * get our data, but let's leave it here in case we detect something strange
             */
            assertEquals("Mismatched meta-data URL(s)", connectUrl, connURL);
            assertTrackedConnection(conn, connURL);
        } finally {
            conn.close();   // don't need it for anything
            assertConnectionNotTracked(conn);
        }

        return assertConnectDetails(connectDriver, connectUrl, connectProps);
    }

    private Operation assertConnectDetails(Driver driver, String url, Properties props) {
        Operation op = assertConnectDetails(url, "create");
        assertEquals("Mismatched driver class", driver.getClass().getName(), op.get("driverClass", String.class));

        OperationMap actualParams = op.get("params", OperationMap.class);
        if (actualParams != null) {   // OK if missing - means 'collectExtraInformation' is FALSE
            OperationMap expectedParams = JdbcDriverConnectOperationCollectionAspect.addConnectionProperties(new Operation(), props);
            assertEquals("Mismatched parameters size", expectedParams.size(), actualParams.size());
            for (String key : expectedParams.keySet()) {
                Object expValue = expectedParams.get(key), actValue = actualParams.get(key);
                assertEquals("Mismatched value for parameter=" + key, expValue, actValue);
            }
        }

        return op;
    }

    private void assertObscuredProperties(Operation op, Collection<String> obscuredKeys, boolean expectedState) {
        OperationMap params = op.get("params", OperationMap.class);
        if (params == null)
            return;

        JdbcDriverConnectOperationCollectionAspect aspectInstance = getAspect();
        ObscuredValueSetMarker obscuredValue = (ObscuredValueSetMarker) aspectInstance.getSensitiveValueMarker();
        for (String key : obscuredKeys) {
            Object value = params.get(key);
            if (value == null) {
                continue;
            }

            assertEquals("Key=" + key + " obscured state mismatch",
                    Boolean.valueOf(expectedState),
                    Boolean.valueOf(obscuredValue.contains(value)));
        }
    }
}
