/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class InjectMocksWithConstructorAndFinalTest {

    interface Client {
        int aMethod();
    }

    static class Service {
        final Client client;

        public Service(Client client) {
            this.client = client;
        }

        int callClient() {
            return client.aMethod();
        }
    }

    @Mock
    private Client client;

    @InjectMocks
    private Service service;

    private int lastClientHash = 0;
    private int lastServiceHash = 0;


    @Test(invocationCount = 4)
    void inject_mock_should_be_refreshed() {

        // we have correct injected mock
        assertThat(client).isNotNull();
        assertThat(service.client).isNotNull().isSameAs(client);

        // we have new mock
        assertThat(lastClientHash).isNotEqualTo(client.hashCode());
        // we have new InjectMocks filed value
        assertThat(lastServiceHash).isNotEqualTo(service.hashCode());

        // clear mock
        assertThat(service.callClient()).isZero();

        // make some stub
        int i = new Random().nextInt() + 1;
        when(client.aMethod()).thenReturn(i);

        // and test it
        assertThat(service.callClient()).isEqualTo(i);

        verify(client, times(2)).aMethod();

        // remember last mock and InjectMocks field hash
        lastClientHash = client.hashCode();
        lastServiceHash = service.hashCode();
    }
}
