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
package com.springsource.insight.plugin.files.tracker;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.springsource.insight.collection.test.OperationCollectionAspectTestSupport;
import com.springsource.insight.intercept.InterceptConfiguration;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.plugin.files.tracker.AbstractFilesTrackerAspectSupport.CacheKey;
import com.springsource.insight.plugin.files.tracker.AbstractFilesTrackerAspectSupport.FilesCache;
import com.springsource.insight.util.StringUtil;

/**
 *
 */
public abstract class FilesTrackerAspectTestSupport extends OperationCollectionAspectTestSupport {
    protected FilesTrackerAspectTestSupport() {
        super();
    }

    protected void assertFileTrackingOperation(Closeable instance, File file, String opcode, String mode) throws IOException {
        assertFileTrackingOperation(instance, file.getAbsolutePath(), opcode, mode);
    }

    protected Operation assertFileTrackingOperation(Closeable instance, String filePath, String opcode, String mode) throws IOException {
        try {
            Operation op = getLastEntered();
            assertNotNull("No operation extracted", op);
            assertEquals("Mismatched operation type", FilesTrackerDefinitions.TYPE, op.getType());
            assertEquals("Mismatched path", filePath, op.get(FilesTrackerDefinitions.PATH_ATTR, String.class));
            assertEquals("Mismatched opcode", opcode, op.get(FilesTrackerDefinitions.OPTYPE_ATTR, String.class));
            // we expect only the file path to be tracked
            assertEquals("Tracking map too big", 1, AbstractFilesTrackerAspectSupport.trackedFilesMap.size());
            assertTrue("Tracking map does not contain input path",
                    AbstractFilesTrackerAspectSupport.trackedFilesMap.containsValue(filePath));

            if (!StringUtil.isEmpty(mode)) {
                assertEquals("Mismatched mode", mode, op.get(FilesTrackerDefinitions.MODE_ATTR, String.class));
            }

            return op;
        } finally {
            instance.close();

            // after close the tracked files map must be empty - this indirectly tests the closing aspect
            Map<?, ?> trackingMap = AbstractFilesTrackerAspectSupport.trackedFilesMap;
            assertTrue("Tracking map not empty for file=" + filePath + "[" + opcode + "/" + mode + "]: " + trackingMap, trackingMap.isEmpty());
        }
    }

    protected void runSynchronizedAspectPerformance(FileAccessor accessor) {
        for (int threads : new int[]{1, 4}) {
            final Map<CacheKey, String> orgCache = AbstractFilesTrackerAspectSupport.trackedFilesMap;
            try {
                FilesCache cache = new FilesCache(5);
                AbstractFilesTrackerAspectSupport.trackedFilesMap =
                        (threads > 1) ? Collections.synchronizedMap(cache) : cache;
                runSynchronizedAspectPerformance(accessor, threads);
                assertTrue("Tracking map not empty for threads=" + threads + ": " + cache, cache.isEmpty());
            } finally {
                AbstractFilesTrackerAspectSupport.trackedFilesMap = orgCache;
            }
        }
    }

    protected void runSynchronizedAspectPerformance(final FileAccessor accessor, int threads) {
        final Runtime RUNTIME = Runtime.getRuntime();

        System.out.println("-------------- Synchronized=" + (threads > 1) + " Threads: " + threads + " ------------------");
        System.out.printf("%10s %20s %20s %20s", "Num. calls/th", "Duration (nano)", "Duration/th", "Used memory (B)");
        System.out.println();
        for (final int NUM_CALLS : new int[]{100, 1000, 2500}) {
            Runnable runner = new Runnable() {
                public void run() {
                    for (int cIndex = 0; cIndex < NUM_CALLS; cIndex++) {
                        try {
                            Closeable instance = accessor.createInstance();
                            instance.close();
                        } catch (Exception e) {
                            fail(e.getClass().getSimpleName() + " error while running multi-threaded test"
                                    + " at index=" + cIndex + " out of " + NUM_CALLS + " calls: " + e.getMessage());
                        }
                    }
                    InterceptConfiguration.getInstance().getFrameBuilder().dump();
                }
            };
            ExecutorService exec = Executors.newFixedThreadPool(threads);
            encourageGC();
            List<Future<?>> futureList = new ArrayList<Future<?>>(threads);
            long startTime = System.nanoTime(), startFree = RUNTIME.freeMemory();

            for (int i = 0; i < threads; i++)
                futureList.add(exec.submit(runner));
            for (Future<?> f : futureList) {
                try {
                    // see javadoc for ExecutorService#submit...
                    assertNullValue("Unexpected Future termination value", f.get());
                } catch (Exception e) {
                    fail(e.getClass().getSimpleName() + " error while checking multi-threaded termination: " + e.getMessage());
                }
            }
            long endTime = System.nanoTime(), endFree = RUNTIME.freeMemory();
            System.out.printf("%10d %20d %20d %20d",
                    Integer.valueOf(NUM_CALLS), Long.valueOf(endTime - startTime),
                    Long.valueOf((endTime - startTime) / threads),
                    Long.valueOf(startFree - endFree));
            System.out.println();
        }
    }

    protected static interface FileAccessor {
        Closeable createInstance() throws IOException;
    }
}
