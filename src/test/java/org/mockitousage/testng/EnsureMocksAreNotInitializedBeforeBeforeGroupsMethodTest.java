/*
 * Copyright (c) 2021 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Observer;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class EnsureMocksAreNotInitializedBeforeBeforeGroupsMethodTest {

    @Mock
    Observer observer;

    @BeforeGroups
    private void make_sure_mock_is_not_initialized() {
        assertThat(observer).isNull();
    }

    @Test
    public void dummy_test1() {
        assertThat(observer).isNotNull();
    }

    @Test
    public void dummy_test2() {
        assertThat(observer).isNotNull();
    }
}
