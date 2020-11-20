/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.testng.MockitoTestNGListener;
import org.mockitousage.testng.failuretests.HasUnusedStubs;
import org.mockitousage.testng.utils.TestNGRunner;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Listeners(MockitoTestNGListener.class)
public class StrictStubsTest {

    @Mock List list;

    @Test public void detects_potential_stubbing_problem() {
        when(list.add("a")).thenReturn(true);

        Assertions.assertThatThrownBy(() -> StrictStubsCode.testStrictStubs(list, "b"))
                .isInstanceOf(PotentialStubbingProblem.class);
    }

    @Test public void detects_unused_stubs() {
        //when
        TestNGRunner.Result result = new TestNGRunner().run(HasUnusedStubs.class);

        //then
        assertThat(result.getFailure()).isInstanceOf(UnnecessaryStubbingException.class);
    }

    @Test public void keeps_tests_dry() {
        //when
        when(list.add("a")).thenReturn(true);
        list.add("a");

        //then used stubbing is implicitly verified
        verifyNoMoreInteractions(list);
    }
}
