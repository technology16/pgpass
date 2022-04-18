package ru.taximaxim.pgpass;


import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

public class PgPassTest {
    private final Path gpPassPathWildcard;
    private final Path gpPassPath;
    private final Path gpPassPathWildcardEscape;

    /*
     * found at  https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     * */
    @SuppressWarnings({"unchecked"})
    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }

    @SuppressWarnings({"unchecked"})
    public static void removeEnv(String name) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).remove(name);
    }

    static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static void delete(Path dest) {
        try {
            Files.deleteIfExists(dest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public PgPassTest() throws URISyntaxException {
        gpPassPathWildcard = Paths.get(Objects.requireNonNull(getClass().getResource("/pgpass_wildcard")).toURI());
        gpPassPath = Paths.get(Objects.requireNonNull(getClass().getResource("/pgpass")).toURI());
        gpPassPathWildcardEscape = Paths.get(Objects.requireNonNull(getClass().getResource("/pgpass_wildcard_escape")).toURI());

    }

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
    public void test_env() throws ReflectiveOperationException {
        updateEnv("PGPASSFILE", "/a/sample/path/pgpass");
        assertEquals("/a/sample/path/pgpass", System.getenv("PGPASSFILE"));
        removeEnv("PGPASSFILE");
        assertFalse(System.getenv().containsKey("PGPASSFILE"));
    }

    @Test
    public void testEnvPGPASSFILE() throws PgPassException, ReflectiveOperationException {
        updateEnv("PGPASSFILE", gpPassPath.toAbsolutePath().toString());
        assertNull(PgPass.get("anything.test", "5432", "db1", "user1"));
    }

    @Test
    public void testEnvMissing() throws PgPassException, ReflectiveOperationException {
        removeEnv("PGPASSFILE");
        Path user_pgpass = Paths.get("/root/.pgpass");
        delete(user_pgpass);
        copy(gpPassPath, user_pgpass);
        assertNull(PgPass.get("anything.test", "5432", "db1", "user1"));
    }

}
