package org.mockitousage.testng.failuretests;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.when;

@Listeners(MockitoTestNGListener.class)
@Test(description = "Always failing, shouldn't be listed in 'mockito-testng.xml'")
public class HasUnusedStubs {
    @Mock
    List<String> mock;
    @Test public void test() {
        when(mock.add("a")).thenReturn(true);
    }
}
