/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class ParallelMethodTest {

    @Test(threadPoolSize = 4, invocationCount = 8)
    public void testParallel() {
        // nothing to do here, the execution just shouldn't fail
    }

}
