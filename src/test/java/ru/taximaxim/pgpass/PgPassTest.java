package ru.taximaxim.pgpass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PgPassTest {
    private final Path gpPassPathWildcard = Paths.get(getClass().getResource("/pgpass_wildcard").getPath());
    private final Path gpPassPath = Paths.get(getClass().getResource("/pgpass").getPath());
    private final Path gpPassPathWildcardEscape = Paths.get(getClass().getResource("/pgpass_wildcard_escape").getPath());

    @Test
    public void testHostnameExistingIp() throws PgPassException {
        assertEquals("777", PgPass.get(gpPassPathWildcard, "127.0.0.1", "5432", "db1", "user1"));
    }

    @Test
    public void testHostnameExisting() throws PgPassException {
        assertEquals("888", PgPass.get(gpPassPathWildcard, "my.test", "5432", "db1", "user1"));
    }

    @Test
    public void testHostnameExistingWildcard() throws PgPassException {
        assertEquals("999", PgPass.get(gpPassPathWildcard, "anything.test", "5432", "db1", "user1"));
    }

    @Test
    public void testHostnameMissing() throws PgPassException {
        assertNull(PgPass.get(gpPassPath, "anything.test", "5432", "db1", "user1"));
    }

    @Test
    public void testHaveEscapeSymbols() throws PgPassException {
        assertEquals("9\\9:9", PgPass.get(gpPassPathWildcardEscape, "anything.test", "5432", "db:1", "u:ser\\1"));
    }

    @Test
    public void testHaveEscapeSymbolsHostname() throws PgPassException {
        assertEquals("7\\:7\\7", PgPass.get(gpPassPathWildcardEscape, "127\\.0:.0.1", "5432", "db1", "user1"));
    }

    @Test
    public void testReadAll() throws PgPassException {
        List<PgPassEntry> predefined = new ArrayList<>();

        predefined.add(new PgPassEntry("127.0.0.1", "*", "db1", "user1", "777"));
        predefined.add(new PgPassEntry("my.test", "*", "db1", "user1", "888"));
        predefined.add(new PgPassEntry("*", "*", "db1", "user1", "999"));

        assertEquals(predefined, PgPass.getAll(gpPassPathWildcard));
    }
}
