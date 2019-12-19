/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

class FilePreviewGetPreviewDependency {
    BufferedReader bufferedReader(String filePath, Charset encoding) throws IOException {
        return new BufferedReader(new FileReader(filePath, encoding));
    }
}
