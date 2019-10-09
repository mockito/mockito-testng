/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class LenientStubsTest {

    @Mock List list;

    @Test public void does_not_detect_potential_stubbing_problem() {
        when(list.add("a")).thenReturn(true);

        list.add("b");
    }

    @Test public void does_not_detect_unused_stubs() {
        when(list.add("a")).thenReturn(true);
    }
}
