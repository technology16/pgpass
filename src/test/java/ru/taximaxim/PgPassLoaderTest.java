package ru.taximaxim;

import org.junit.Test;
import ru.taximaxim.PgPassLoader.PgPass;
import ru.taximaxim.PgPassLoader.PgPassException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PgPassLoaderTest {
    private final Path gpPassPathWildcard = Paths.get(getClass().getResource("/pgpass_wildcard").getPath());
    private final Path gpPassPath = Paths.get(getClass().getResource("/pgpass").getPath());

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
}
