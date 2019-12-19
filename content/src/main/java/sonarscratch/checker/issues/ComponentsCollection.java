/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class ComponentsCollection implements Iterable<JSONObject> {
    private ArrayList<JSONObject> list = new ArrayList<>();

    public void addComponent(JSONObject component) {
        list.add(component);
    }

    public int count() {
        return list.size();
    }

    public Iterator<JSONObject> iterator() {
        return list.iterator();
    }
}
