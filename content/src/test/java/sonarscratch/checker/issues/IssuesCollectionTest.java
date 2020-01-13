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

public class IssuesCollectionTest {
    @Test
    public void test_Total() {
        final int TOTAL = 258;

        assertEquals(TOTAL, new IssuesCollection(TOTAL).total());
    }

    @Test
    public void test_AddIssue() {
        var issues = new IssuesCollection(0);

        issues.addIssue(null);

        var jsonObject = new JSONObject();

        issues.addIssue(jsonObject);
        var i = 0;

        for (JSONObject item : issues) {
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
        assertEquals(0, new IssuesCollection(0).count());
    }

    @Test
    public void test_Count_() {
        var issues = new IssuesCollection(0);

        issues.addIssue(null);
        issues.addIssue(null);

        final int ISSUES_COUNT = 2;
        assertEquals(ISSUES_COUNT, issues.count());
    }

    @Test
    public void test_Iterator_0() {
        var issues = new IssuesCollection(0);
        var count = 0;

        for (JSONObject item : issues) {
            if (item == null) {
                count++;
            }
        }

        assertEquals(0, count);
    }

    @Test
    public void test_Iterator_1() {
        var issues = new IssuesCollection(0);

        issues.addIssue(null);

        var count = 0;

        for (JSONObject item : issues) {
            if (item == null) {
                count++;
            }
        }

        assertEquals(1, count);
    }

    @Test
    public void test_Iterator_() {
        var issues = new IssuesCollection(0);

        issues.addIssue(null);
        issues.addIssue(null);

        var count = 0;

        for (JSONObject item : issues) {
            if (item == null) {
                count++;
            }
        }

        final int ISSUES_COUNT = 2;
        assertEquals(ISSUES_COUNT, count);
    }
}
