/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng.failuretests;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;

import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Should fail.
 *
 * @see TestNGShouldFailWhenMockitoListenerFailsTest
 */
@Listeners(MockitoTestNGListener.class)
@Test(description = "Always failing, shouldn't be listed in 'mockito-testng.xml'")
public class FailingOnPurposeBecauseIncorrectStubbingSyntax {

    public void incorrect_stubbing_syntax_in_test() {
        anyString();
        anySet();
    }

}
