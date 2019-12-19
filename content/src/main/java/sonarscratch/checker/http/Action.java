/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

import java.io.InputStream;

@FunctionalInterface
public interface Action {
    void run(InputStream stream) throws ActionException;
}
