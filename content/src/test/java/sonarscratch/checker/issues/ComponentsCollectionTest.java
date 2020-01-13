/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;

public class ComponentsCollectionTest {
    @Test
    public void test_AddComponent() {
        var components = new ComponentsCollection();

        components.addComponent(null);

        var jsonObject = new JSONObject();

        components.addComponent(jsonObject);
        var i = 0;

        for (JSONObject item : components) {
            if (i == 0) {
                assertNull(item);
            } else {
                assertEquals(jsonObject, item);
            }
            i++;
        }
    }

    @Test
    public void test_Count_0() {
        assertEquals(0, new ComponentsCollection().count());
    }

    @Test
    public void test_Count_() {
        var components = new ComponentsCollection();

        components.addComponent(null);
        components.addComponent(null);

        final int COMPONENTS_COUNT = 2;
        assertEquals(COMPONENTS_COUNT, components.count());
    }

    @Test
    public void test_Iterator_0() {
        var components = new ComponentsCollection();
        var count = 0;

        for (JSONObject item : components) {
            if (item == null) {
                count++;
            }
        }

        assertEquals(0, count);
    }

    @Test
    public void test_Iterator_1() {
        var components = new ComponentsCollection();

        components.addComponent(null);

        var count = 0;

        for (JSONObject item : components) {
            if (item == null) {
                count++;
            }
        }

        assertEquals(1, count);
    }

    @Test
    public void test_Iterator_() {
        var components = new ComponentsCollection();

        components.addComponent(null);
        components.addComponent(null);

        var count = 0;

        for (JSONObject item : components) {
            if (item == null) {
                count++;
            }
        }

        final int COMPONENTS_COUNT = 2;
        assertEquals(COMPONENTS_COUNT, count);
    }
}
