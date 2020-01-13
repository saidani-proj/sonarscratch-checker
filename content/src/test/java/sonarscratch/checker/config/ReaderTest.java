/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReaderTest {
    @Rule
    public final ExpectedException expectedExceptionRule = ExpectedException.none();
    private static final String PROJECT_ARGUMENT = "--project:";

    @Test
    public void test_Read_CorrectArgs_WhiteSpaces() throws ReaderException {
        assertTrue(new Reader(new String[] { "-r", "" }).read().writeReport());
    }

    @Test
    public void test_Read_CorrectArgs_Help_Long() throws ReaderException {
        assertTrue(new Reader(new String[] { "--help" }).read().showHelp());
    }

    @Test
    public void test_Read_CorrectArgs_Help_Short() throws ReaderException {
        assertTrue(new Reader(new String[] { "-h" }).read().showHelp());
    }

    @Test
    public void test_Read_CorrectArgs_Version_Long() throws ReaderException {
        assertTrue(new Reader(new String[] { "--version" }).read().showVersion());
    }

    @Test
    public void test_Read_CorrectArgs_Version_Short() throws ReaderException {
        assertTrue(new Reader(new String[] { "-v" }).read().showVersion());
    }

    @Test
    public void test_Read_CorrectArgs_Url() throws ReaderException {
        final String URL = "https://sonarcloud.io:9000";

        assertEquals(URL, new Reader(new String[] { "--url", URL }).read().getSonarUrl());
    }

    @Test
    public void test_Read_CorrectArgs_Count() throws ReaderException {
        final int COUNT = 15;

        assertEquals(COUNT,
                new Reader(new String[] { "--count", Integer.toString(COUNT) }).read().getSonarAttemptsCount());
    }

    @Test
    public void test_Read_CorrectArgs_Sleep() throws ReaderException {
        final int SLEEP = 500;

        assertEquals(SLEEP, new Reader(new String[] { "--sleep", Integer.toString(SLEEP) }).read()
                .getSonarAttemptSleepMilliseconds());
    }

    @Test
    public void test_Read_CorrectArgs_Report_Long() throws ReaderException {
        assertTrue(new Reader(new String[] { "--report" }).read().writeReport());
    }

    @Test
    public void test_Read_CorrectArgs_Report_Short() throws ReaderException {
        assertTrue(new Reader(new String[] { "-r" }).read().writeReport());
    }

    @Test
    public void test_Read_CorrectArgs_Path() throws ReaderException {
        final String PATH = "output-test.html";

        assertEquals(PATH, new Reader(new String[] { "--path", PATH }).read().getReportPath());
    }

    private static <T> int getIterableCount(Iterable<T> iterable) {
        var iterator = iterable.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            count++;
            iterator.next();
        }

        return count;
    }

    @Test
    public void test_Read_CorrectArgs_Project_0() throws ReaderException {
        assertEquals(0, getIterableCount(new Reader(new String[0]).read().getProjects()));
    }

    @Test
    public void test_Read_CorrectArgs_Project_1() throws ReaderException {
        final String PROJECT_NAME = "src:key";
        final String PROJECT_ROOT_PATH = "src/value";

        var projects = new Reader(new String[] { PROJECT_ARGUMENT + PROJECT_NAME, PROJECT_ROOT_PATH }).read()
                .getProjects();

        assertEquals(1, getIterableCount(projects));

        for (Project project : projects) {
            assertEquals(PROJECT_NAME, project.getName());
            assertEquals(PROJECT_ROOT_PATH, project.getRootPath());
        }
    }

    @Test
    public void test_Read_CorrectArgs_Project_2() throws ReaderException {
        final String PROJECT1_NAME = "src:key1";
        final String PROJECT1_ROOT_PATH = "src/value1";
        final String PROJECT2_NAME = "src:key2";
        final String PROJECT2_ROOT_PATH = "src/value2";

        var projects = new Reader(new String[] { PROJECT_ARGUMENT + PROJECT1_NAME, PROJECT1_ROOT_PATH,
                PROJECT_ARGUMENT + PROJECT2_NAME, PROJECT2_ROOT_PATH }).read().getProjects();

        final int PROJECTS_COUNT = 2;
        assertEquals(PROJECTS_COUNT, getIterableCount(projects));

        int i = 0;

        for (Project project : projects) {
            assertEquals(i == 0 ? PROJECT1_NAME : PROJECT2_NAME, project.getName());
            assertEquals(i == 0 ? PROJECT1_ROOT_PATH : PROJECT2_ROOT_PATH, project.getRootPath());

            i++;
        }
    }

    @Test
    public void test_Read_IncorrectArgs_Incoherent() throws ReaderException {
        expectedExceptionRule.expect(ReaderException.class);
        expectedExceptionRule.expectMessage("Out of arguments");

        new Reader(new String[] { "--path" }).read().getReportPath();
    }

    @Test
    public void test_Read_IncorrectArgs_Unknown() throws ReaderException {
        expectedExceptionRule.expect(ReaderException.class);
        expectedExceptionRule.expectMessage("Unknown argument '--unknown'");

        new Reader(new String[] { "--unknown" }).read().getReportPath();
    }

    @Test
    public void test_On_Url_Incorrect() throws ReaderException {
        expectedExceptionRule.expect(ReaderException.class);
        expectedExceptionRule.expectMessage("Unknown argument '--unknown'");

        new Reader(new String[] { "--unknown" }).read().getReportPath();
    }

    @Test
    public void test_Read_CorrectArgs_Encoding_Long() throws ReaderException {
        final String ENCODING = "windows-1252";

        assertEquals(ENCODING, new Reader(new String[] { "--encoding", ENCODING }).read().getEncoding());
    }

    @Test
    public void test_Read_CorrectArgs_Encoding_Short() throws ReaderException {
        final String ENCODING = "windows-1252";

        assertEquals(ENCODING, new Reader(new String[] { "-e", ENCODING }).read().getEncoding());
    }

    @Test
    public void test_Read_CorrectArgs_Check_Long() throws ReaderException {
        assertTrue(new Reader(new String[] { "--check" }).read().check());
    }

    @Test
    public void test_Read_CorrectArgs_Check_Short() throws ReaderException {
        assertTrue(new Reader(new String[] { "-c" }).read().check());
    }
}
