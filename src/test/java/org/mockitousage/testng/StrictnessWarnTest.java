/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mock;
import org.mockito.quality.Strictness;
import org.mockito.testng.MockitoSettings;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
@MockitoSettings(strictness = Strictness.WARN)
public class StrictnessWarnTest {

    @Mock
    private List<String> list;

    @BeforeMethod
    void setup() {
        when(list.add("a")).thenReturn(true);
    }

    @Test
    void not_used_stub_generate_warn() {
        //
    }
}
