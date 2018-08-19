/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.when;

@Listeners(MockitoTestNGListener.class)
public class StrictStubsTest {

    @Mock List list;

    @Test public void detects_potential_stubbing_problem() {
        when(list.add("a")).thenReturn(true);

        Assertions.assertThatThrownBy(() -> list.add("b")).isInstanceOf(PotentialStubbingProblem.class);
    }
}
