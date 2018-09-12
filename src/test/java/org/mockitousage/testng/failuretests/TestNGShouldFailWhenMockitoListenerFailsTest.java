/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng.failuretests;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockitousage.testng.utils.TestNGRunner;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test(
        singleThreaded = true,
        description = "Test that failing tests report a Mockito exception"
)
public class TestNGShouldFailWhenMockitoListenerFailsTest {

    private final TestNGRunner runner = new TestNGRunner();

    public void report_failure_on_incorrect_annotation_usage() {
        //when
        TestNGRunner.Result result = runner.run(FailingOnPurposeBecauseIncorrectAnnotationUsage.class);

        //then
        assertThat(result.getFailure()).isInstanceOf(MockitoException.class);
    }

    @Test
    public void report_failure_on_incorrect_stubbing_syntax_with_matchers_in_test_methods() {
        //when
        TestNGRunner.Result result = runner.run(FailingOnPurposeBecauseIncorrectStubbingSyntax.class);

        //then
        assertThat(result.getFailure()).isInstanceOf(InvalidUseOfMatchersException.class);
    }

    @Test
    public void report_failure_on_incorrect_stubbing_syntax_with_matchers_in_configuration_methods() throws Exception {
        //when
        TestNGRunner.Result result = runner.run(FailingOnPurposeBecauseWrongStubbingSyntaxInConfigurationMethod.class);

        //then
        assertThat(result.getFailure()).isInstanceOf(InvalidUseOfMatchersException.class);
    }
}
