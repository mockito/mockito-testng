/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.testng.internal;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Fields;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;

import java.util.Collection;
import java.util.Map;

import static org.mockito.internal.util.reflection.Fields.annotatedBy;

public class MockitoAfterTestNGMethod {

    private final Map<Object, MockitoSession> sessions;

    public MockitoAfterTestNGMethod(Map<Object, MockitoSession> sessions) {
        this.sessions = sessions;
    }

    public void applyFor(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            MockitoSession mockitoSession = sessions.get(testResult.getInstance());
            if (mockitoSession != null) {
                mockitoSession.finishMocking(testResult.getThrowable());
                sessions.remove(testResult.getInstance());
            }
            resetMocks(testResult.getInstance());
        }
    }


    private void resetMocks(Object instance) {
        Mockito.reset(instanceMocksOf(instance).toArray());
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> instanceMocksOf(Object instance) {
        return Fields.allDeclaredFieldsOf(instance)
                                            .filter(annotatedBy(Mock.class, Spy.class))
                                            .notNull()
                                            .assignedValues();
    }


}
