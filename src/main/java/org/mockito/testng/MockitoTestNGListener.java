/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.testng;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGListener;
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
 * <code>MockitoTestNGListener</code> not working with parallel tests,
 * more information https://github.com/mockito/mockito-testng/issues/20
 * </p>
 */
public class MockitoTestNGListener implements IInvokedMethodListener {

    private final Map<Object, MockitoSession> sessions = new WeakHashMap<>();

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (hasMockitoTestNGListenerInTestHierarchy(testResult)) {
            sessions.computeIfAbsent(testResult.getInstance(), testInstance ->
                    Mockito.mockitoSession()
                            .initMocks(testInstance)
                            .strictness(Strictness.STRICT_STUBS)
                            .startMocking()
            );
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (hasMockitoTestNGListenerInTestHierarchy(testResult) && method.isTestMethod()) {
            Optional.ofNullable(sessions.remove(testResult.getInstance()))
                    .ifPresent(mockitoSession -> mockitoSession.finishMocking(testResult.getThrowable()));
        }
    }

    protected boolean hasMockitoTestNGListenerInTestHierarchy(ITestResult testResult) {
        for (Class<?> clazz = testResult.getTestClass().getRealClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (hasMockitoTestNGListener(clazz)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasMockitoTestNGListener(Class<?> clazz) {
        Listeners listeners = clazz.getAnnotation(Listeners.class);
        if (listeners == null) {
            return false;
        }

        for (Class<? extends ITestNGListener> listenerClass : listeners.value()) {
            if (listenerClass() == listenerClass) {
                return true;
            }
        }
        return false;
    }

    protected Class<MockitoTestNGListener> listenerClass() {
        return MockitoTestNGListener.class;
    }

}
