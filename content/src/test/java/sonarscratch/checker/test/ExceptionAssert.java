/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class ExceptionAssert {
    private ExceptionAssert() {
    }

    public static void checkType(Class<?> type, Throwable exception) {
        assertNotNull(exception);

        if (exception != null) {
            assertTrue(exception.getClass().isAssignableFrom(type));
        }
    }

    public static void checkMessage(String message, Throwable exception) {
        assertNotNull(exception);

        if (exception != null) {
            assertEquals(message, exception.getMessage());
        }
    }
}
