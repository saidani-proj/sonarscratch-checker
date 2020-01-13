/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.config;

public class Project {
    private String name;
    private String rootPath;

    public Project(String name, String rootPath) {
        this.name = name;
        this.rootPath = rootPath;
    }

    public String getName() {
        return name;
    }

    public String getRootPath() {
        return rootPath;
    }
}
