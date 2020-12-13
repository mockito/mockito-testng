/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.testng;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.internal.util.reflection.Fields;
import org.mockito.internal.util.reflection.InstanceField;
import org.mockito.quality.Strictness;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;

/**
 * <p>Mockito TestNG Listener, this listener initializes mocks and handles strict stubbing, it is similar to JUnit
 * <code>MockitoJUnitRunner</code>, <code>MockitoRule</code>, <code>MockitoExtension</code> and adds
 * the following behavior to your test:</p>
 *
 * <ul>
 *     <li>
 *         Before any TestNG method, either a <em>configuration method</em> (&#064;BeforeMethod, &#064;BeforeClass, etc)
 *         or a <em>test</em> method MockitoSession is started by:
 *         <pre class="code"><code class="java">
 *         Mockito.mockitoSession()
 *          .initMocks(testInstance)
 *          .strictness(Strictness.STRICT_STUBS)
 *          .startMocking()
 *         </code></pre>
 *         See javadoc {@link MockitoSession}
 *     </li>
 *     <li>
 *         After each <em>test</em> method {@link MockitoSession#finishMocking()} is called.
 *     </li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre class="code"><code class="java">
 * <b>&#064;Listeners(MockitoTestNGListener.class)</b>
 * public class ExampleTest {
 *
 *     &#064;Mock
 *     private List list;
 *
 *     &#064;Test
 *     public void shouldDoSomething() {
 *         list.add(100);
 *     }
 * }
 * </code></pre>
 *
 * <p>
 * By default {@link MockitoSession} is started with {@link Strictness#STRICT_STUBS}.
 * You can change this behavior by adding {@link MockitoSettings} to your test class.
 * </p>
 *
 * <pre class="code"><code class="java">
 * <b>&#064;Listeners(MockitoTestNGListener.class)</b>
 * <b>&#064;MockitoSettings(strictness = Strictness.WARN)</b>
 * public class ExampleTest {
 *  ...
 * }
 * </code></pre>
 *
 * <p>
 * <code>MockitoTestNGListener</code> not working with parallel tests,
 * more information https://github.com/mockito/mockito-testng/issues/20
 * </p>
 */
public class MockitoTestNGListener implements IInvokedMethodListener {

    private final Map<Object, MockitoSession> sessions = new HashMap<>();
    private final Map<Object, Map<InstanceField, Object>> injectMocksFieldsValues = new HashMap<>();

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (shouldBeRunBeforeInvocation(method, testResult)) {

            // save value of all InjectMocks fields
            // in order to restore state before next tests
            // https://github.com/mockito/mockito-testng/issues/28
            injectMocksFieldsValues.computeIfAbsent(testResult.getInstance(), testInstance ->
                    Fields.allDeclaredFieldsOf(testInstance).instanceFields()
                            .stream()
                            .filter(field -> field.isAnnotatedBy(InjectMocks.class))
                            .collect(HashMap::new, (m, v) -> m.put(v, v.read()), HashMap::putAll));

            sessions.computeIfAbsent(testResult.getInstance(), testInstance -> {

                        Strictness strictness = findAnnotation(testResult, MockitoSettings.class)
                                .map(MockitoSettings::strictness).orElse(Strictness.STRICT_STUBS);

                        // start MockitoSession
                        return Mockito.mockitoSession()
                                .initMocks(testInstance)
                                .strictness(strictness)
                                .startMocking();
                    }
            );
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (shouldBeRunAfterInvocation(method, testResult)) {
            try {
                Optional.ofNullable(sessions.remove(testResult.getInstance()))
                        .ifPresent(mockitoSession -> mockitoSession.finishMocking(testResult.getThrowable()));
            } finally {
                Optional.ofNullable(injectMocksFieldsValues.remove(testResult.getInstance()))
                        .ifPresent(fieldsValues -> fieldsValues.forEach(InstanceField::set));
            }
        }
    }

    private boolean shouldBeRunBeforeInvocation(IInvokedMethod method, ITestResult testResult) {
        return !isAfterConfigurationMethod(method) && hasMockitoTestNGListener(testResult);
    }

    private boolean isAfterConfigurationMethod(IInvokedMethod method) {
        ITestNGMethod testMethod = method.getTestMethod();
        return testMethod.isAfterClassConfiguration()
                || testMethod.isAfterMethodConfiguration()
                || testMethod.isAfterGroupsConfiguration()
                || testMethod.isAfterTestConfiguration()
                || testMethod.isAfterSuiteConfiguration();
    }

    private boolean shouldBeRunAfterInvocation(IInvokedMethod method, ITestResult testResult) {
        return method.isTestMethod() && hasMockitoTestNGListener(testResult);
    }

    protected boolean hasMockitoTestNGListener(ITestResult testResult) {

        return findAnnotation(testResult, Listeners.class)
                .map(Listeners::value)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .anyMatch(listener -> listener == MockitoTestNGListener.class);
    }

    <A extends Annotation> Optional<A> findAnnotation(ITestResult testResult, Class<A> annotationClass) {

        for (Class<?> clazz = testResult.getTestClass().getRealClass();
             clazz != Object.class; clazz = clazz.getSuperclass()) {
            Optional<A> annotation = Optional.ofNullable(clazz.getAnnotation(annotationClass));
            if (annotation.isPresent()) {
                return annotation;
            }
        }

        return Optional.empty();
    }
}
