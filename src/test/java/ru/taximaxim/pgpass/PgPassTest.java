package ru.taximaxim.pgpass;

import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PgPassTest {
    private final Path gpPassPathWildcard;
    private final Path gpPassPath;
    private final Path gpPassPathWildcardEscape;

    public PgPassTest() throws URISyntaxException {
        gpPassPathWildcard = Paths.get(Objects.requireNonNull(getClass().getResource("/pgpass_wildcard")).toURI());
        gpPassPath = Paths.get(Objects.requireNonNull(getClass().getResource("/pgpass")).toURI());
        gpPassPathWildcardEscape = Paths.get(Objects.requireNonNull(getClass().getResource("/pgpass_wildcard_escape")).toURI());
    }

    @Test
    public void testHostnameExistingIp() throws PgPassException {
        assertEquals("777", PgPass.get(gpPassPathWildcard, "127.0.0.1", "5432", "db1", "user1"));
        assertEquals("777", PgPass.get("127.0.0.1", "5432", "db1", "user1"));
        assertEquals("999", PgPass.get("127.0.0.1", "5432", "db1", "user2"));
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
        assertNull(PgPass.get( "anything.test", "5432", "db1", "user1"));
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

//    @Test
//    public void testReadAll() throws PgPassException {
//        List<PgPassEntry> predefined = new ArrayList<>();
//
//        predefined.add(new PgPassEntry("127.0.0.1", "*", "db1", "user1", "777"));
//        predefined.add(new PgPassEntry("my.test", "*", "db1", "user1", "888"));
//        predefined.add(new PgPassEntry("*", "*", "db1", "user1", "999"));
//
//        assertEquals(predefined, PgPass.getAll(gpPassPathWildcard));
//    }
}
