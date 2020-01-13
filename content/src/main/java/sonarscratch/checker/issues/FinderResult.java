/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

public class FinderResult {
    private IssuesCollection issuesCollection;
    private ComponentsCollection componentsCollection;

    FinderResult(IssuesCollection issuesCollection, ComponentsCollection componentsCollection) {
        this.issuesCollection = issuesCollection;
        this.componentsCollection = componentsCollection;
    }

    public IssuesCollection getIssuesCollection() {
        return issuesCollection;
    }

    public ComponentsCollection getComponentsCollection() {
        return componentsCollection;
    }
}
