/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.util;

public final class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static <T> String getDefaultMessage(Class<T> cls) {
        return "Exception in class " + cls.getName();
    }
}
