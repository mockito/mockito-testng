package org.mockitousage.testng.failuretests;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(MockitoTestNGListener.class)
public class HasUnusedStubsInSetup {

    @Mock
    List<String> mock;

    @BeforeMethod
    public void setup() {
        when(mock.add("a")).thenReturn(true);
    }

    @Test(priority = 1)
    public void testPass() {
        assertThat(mock.add("a")).isTrue();
    }

    @Test(priority = 2)
    public void testFail() {
        // stub from setup is not used
    }

}
