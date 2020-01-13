/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

class IssueFile {
    private String name;
    private String path;

    IssueFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    String getName() {
        return name;
    }

    String getPath() {
        return path;
    }
}
