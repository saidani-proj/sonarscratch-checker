/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class IssuesCollection implements Iterable<JSONObject> {
    private ArrayList<JSONObject> list = new ArrayList<>();
    private int total;

    public IssuesCollection(int total) {
        this.total = total;
    }

    public int total() {
        return total;
    }

    public void addIssue(JSONObject issue) {
        list.add(issue);
    }

    public int count() {
        return list.size();
    }

    public Iterator<JSONObject> iterator() {
        return list.iterator();
    }
}
