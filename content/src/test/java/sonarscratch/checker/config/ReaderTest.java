/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
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
    public final ExpectedException expectedException = ExpectedException.none();

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
        final String url = "https://sonarcloud.io:9000";

        assertEquals(url, new Reader(new String[] { "--url", url }).read().getSonarUrl());
    }

    @Test
    public void test_Read_CorrectArgs_Count() throws ReaderException {
        final int count = 15;

        assertEquals(count,
                new Reader(new String[] { "--count", Integer.toString(count) }).read().getSonarAttemptsCount());
    }

    @Test
    public void test_Read_CorrectArgs_Sleep() throws ReaderException {
        final int sleep = 500;

        assertEquals(sleep, new Reader(new String[] { "--sleep", Integer.toString(sleep) }).read()
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
        final String path = "output-test.html";

        assertEquals(path, new Reader(new String[] { "--path", path }).read().getReportPath());
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
        final String projectName = "src:key";
        final String projectRootPath = "src/value";

        var projects = new Reader(new String[] { "--project:" + projectName, projectRootPath }).read().getProjects();

        assertEquals(1, getIterableCount(projects));

        for (Project project : projects) {
            assertEquals(projectName, project.getName());
            assertEquals(projectRootPath, project.getRootPath());
        }
    }

    @Test
    public void test_Read_CorrectArgs_Project_2() throws ReaderException {
        final String project1Name = "src:key1";
        final String project1RootPath = "src/value1";
        final String project2Name = "src:key2";
        final String project2RootPath = "src/value2";

        var projects = new Reader(new String[] { "--project:" + project1Name, project1RootPath,
                "--project:" + project2Name, project2RootPath }).read().getProjects();

        assertEquals(2, getIterableCount(projects));

        int i = 0;

        for (Project project : projects) {
            assertEquals(i == 0 ? project1Name : project2Name, project.getName());
            assertEquals(i == 0 ? project1RootPath : project2RootPath, project.getRootPath());

            i++;
        }
    }

    @Test
    public void test_Read_IncorrectArgs_Incoherent() throws ReaderException {
        expectedException.expect(ReaderException.class);
        expectedException.expectMessage("Out of arguments");

        new Reader(new String[] { "--path" }).read().getReportPath();
    }

    @Test
    public void test_Read_IncorrectArgs_Unknown() throws ReaderException {
        expectedException.expect(ReaderException.class);
        expectedException.expectMessage("Unknown argument '--unknown'");

        new Reader(new String[] { "--unknown" }).read().getReportPath();
    }

    @Test
    public void test_On_Url_Incorrect() throws ReaderException {
        expectedException.expect(ReaderException.class);
        expectedException.expectMessage("Unknown argument '--unknown'");

        new Reader(new String[] { "--unknown" }).read().getReportPath();
    }

    @Test
    public void test_Read_CorrectArgs_Encoding_Long() throws ReaderException {
        final String encoding = "windows-1252";

        assertEquals(encoding, new Reader(new String[] { "--encoding", encoding }).read().getEncoding());
    }

    @Test
    public void test_Read_CorrectArgs_Encoding_Short() throws ReaderException {
        final String encoding = "windows-1252";

        assertEquals(encoding, new Reader(new String[] { "-e", encoding }).read().getEncoding());
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
