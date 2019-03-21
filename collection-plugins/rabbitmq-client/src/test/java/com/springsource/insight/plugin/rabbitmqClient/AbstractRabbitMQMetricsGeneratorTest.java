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

package com.springsource.insight.plugin.rabbitmqClient;

import java.util.List;

import com.springsource.insight.intercept.metrics.AbstractMetricsGenerator;
import com.springsource.insight.intercept.metrics.AbstractMetricsGeneratorTest;
import com.springsource.insight.intercept.metrics.MetricsBag;
import com.springsource.insight.util.IDataPoint;

public abstract class AbstractRabbitMQMetricsGeneratorTest extends AbstractMetricsGeneratorTest {
    protected AbstractRabbitMQMetricsGeneratorTest(AbstractRabbitMetricsGenerator generator) {
        super(generator);
    }

    @Override
    protected void validateMetricsBags(List<MetricsBag> mbs) {
        AbstractRabbitMetricsGenerator genRabbit = (AbstractRabbitMetricsGenerator) gen;
        assertEquals(2, mbs.size());
        assertExternalResourceMetricBag(genRabbit, mbs.get(0));
        assertExternalResourceMetricBag(genRabbit, mbs.get(1));


    }

    private void assertExternalResourceMetricBag(AbstractRabbitMetricsGenerator genRabbit, MetricsBag mb) {
        assertEquals("opExtKey", mb.getResourceKey().getName());
        List<String> keys = mb.getMetricKeys();
        assertEquals(3, keys.size());

        assertTrue(keys.get(0).equals(AbstractMetricsGenerator.EXECUTION_TIME));
        List<IDataPoint> points = mb.getPoints(AbstractMetricsGenerator.EXECUTION_TIME);
        assertEquals(1, points.size());
        assertEquals(160.0, points.get(0).getValue(), 0.5);

        assertTrue(keys.get(1).equals(AbstractMetricsGenerator.INVOCATION_COUNT));
        points = mb.getPoints(AbstractMetricsGenerator.INVOCATION_COUNT);
        assertEquals(1, points.size());
        assertEquals(1.0, points.get(0).getValue(), 0.01);

        assertTrue(keys.get(2).equals(genRabbit.createMetricKey()));
        points = mb.getPoints(genRabbit.createMetricKey());
        assertEquals(1, points.size());
        assertEquals(1d, points.get(0).getValue(), 0);
    }

}