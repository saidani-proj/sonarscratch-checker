/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExceptionUtilTest {
    @Test
    public void test_GetDefaultMessage() {
        assertEquals("Exception in class " + ExceptionUtil.class.getName(),
                ExceptionUtil.getDefaultMessage(ExceptionUtil.class));
    }
}
