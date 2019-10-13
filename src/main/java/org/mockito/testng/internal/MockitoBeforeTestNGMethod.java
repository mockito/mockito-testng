/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.testng.internal;

import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.internal.configuration.CaptorAnnotationProcessor;
import org.mockito.internal.util.reflection.Fields;
import org.mockito.internal.util.reflection.InstanceField;
import org.mockito.quality.Strictness;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;

import java.util.List;
import java.util.Map;

import static org.mockito.testng.MockitoTestNGListener.STRICTNESS_LEVEL;
import static org.mockito.internal.util.reflection.Fields.annotatedBy;

public class MockitoBeforeTestNGMethod {

    private final Map<Object, MockitoSession> sessions;
    private Strictness strictness = Strictness.STRICT_STUBS;

    public MockitoBeforeTestNGMethod(Map<Object, MockitoSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * Initialize mocks.
     *
     * @param method Invoked method.
     * @param testResult TestNG Test Result
     */
    public void applyFor(IInvokedMethod method, ITestResult testResult) {
        initMocks(testResult);
        reinitCaptors(method, testResult);
    }

    private void reinitCaptors(IInvokedMethod method, ITestResult testResult) {
        if (method.isConfigurationMethod()) {
            return;
        }
        initializeCaptors(testResult.getInstance());
    }

    private void initMocks(ITestResult testResult) {
        Object testInstance = testResult.getInstance();
        if (alreadyInitialized(testInstance)) {
            return;
        }

        String strictnessValue = testResult.getTestContext().getSuite().getXmlSuite().getParameter(STRICTNESS_LEVEL);
        if (null != strictnessValue) {
            strictness = Strictness.valueOf(strictnessValue);
        }
        MockitoSession session = Mockito.mockitoSession()
                .initMocks(testInstance)
                .strictness(strictness)
                .startMocking();

        sessions.put(testInstance, session);
    }

    @SuppressWarnings("unchecked")
    private void initializeCaptors(Object instance) {
        List<InstanceField> instanceFields = Fields.allDeclaredFieldsOf(instance).filter(annotatedBy(Captor.class)).instanceFields();
        for (InstanceField instanceField : instanceFields) {
            instanceField.set(new CaptorAnnotationProcessor().process(instanceField.annotation(Captor.class), instanceField.jdkField()));
        }
    }

    private boolean alreadyInitialized(Object instance) {
        return sessions.containsKey(instance);
    }

}
