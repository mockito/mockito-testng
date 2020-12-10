/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class StaticMockTest {

    static class Dummy {
        static String foo() {
            return "foo";
        }
    }

    @Mock
    private MockedStatic<Dummy> dummy;

    @Test
    public void simple_static_mock() {
        assertThat(Dummy.foo()).isNull();
    }

    @Test
    public void verify_static_method() {
        // given
        dummy.when(Dummy::foo).thenReturn("bar");

        // when
        assertThat(Dummy.foo()).isEqualTo("bar");

        // then
        dummy.verify(Dummy::foo);
    }
}
