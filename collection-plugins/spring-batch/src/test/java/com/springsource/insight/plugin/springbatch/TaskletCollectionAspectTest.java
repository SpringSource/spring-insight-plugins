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

package com.springsource.insight.plugin.springbatch;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.springsource.insight.intercept.operation.Operation;


/**
 *
 */
public class TaskletCollectionAspectTest
        extends SpringBatchOperationCollectionAspectTestSupport {
    public TaskletCollectionAspectTest() {
        super();
    }

    @Test
    public void testExecute() throws Exception {
        Tasklet tasklet = new Tasklet() {
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                return RepeatStatus.FINISHED;
            }
        };
        StepContribution contribution = Mockito.mock(StepContribution.class);
        StepExecution stepExecution = createStepExecution("testExecuteJob", "testExecuteStep");
        StepContext stepContext = Mockito.mock(StepContext.class);
        Mockito.when(stepContext.getStepExecution()).thenReturn(stepExecution);

        ChunkContext chunkContext = Mockito.mock(ChunkContext.class);
        Mockito.when(chunkContext.getStepContext()).thenReturn(stepContext);
        tasklet.execute(contribution, chunkContext);

        Operation op = assertOperationDetails(getFirstEntered(), "execute", stepExecution.getStepName());
        assertOperationPath(op, stepExecution);
    }

    @Override
    public TaskletCollectionAspect getAspect() {
        return TaskletCollectionAspect.aspectOf();
    }

}
