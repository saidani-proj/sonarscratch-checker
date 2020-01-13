/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import java.nio.charset.Charset;

class HtmlWriteDependency {
    FilePreview filePreview(String filePath, Charset encoding, Range range) {
        return new FilePreview(filePath, encoding, range);
    }
}
