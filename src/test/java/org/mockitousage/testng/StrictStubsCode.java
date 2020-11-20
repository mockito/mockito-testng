/*
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.testng;

import java.util.List;

/**
 * According to https://github.com/mockito/mockito/pull/1539
 */
public class StrictStubsCode {

    static void testStrictStubs(List list, String value) {
        list.add(value);
    }
}
