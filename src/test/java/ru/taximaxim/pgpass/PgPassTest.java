/*******************************************************************************
 * Copyright 2017-2024 TAXTELECOM, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ru.taximaxim.pgpass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class PgPassTest {

    private final Path gpPassPath = getResource("/pgpass");
    private final Path gpPassPathWildcard = getResource("/pgpass_wildcard");
    private final Path gpPassPathWildcardEscape = getResource("/pgpass_wildcard_escape");

    private static Path getResource(String resourceName) {
        try {
            return Paths.get(PgPassTest.class.getResource(resourceName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
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
    public void testReadAll() throws PgPassException {
        List<PgPassEntry> predefined = new ArrayList<>();

        predefined.add(new PgPassEntry("127.0.0.1", "*", "db1", "user1", "777"));
        predefined.add(new PgPassEntry("my.test", "*", "db1", "user1", "888"));
        predefined.add(new PgPassEntry("*", "*", "db1", "user1", "999"));

        assertEquals(predefined, PgPass.getAll(gpPassPathWildcard));
    }

    // @Test
    public void testEnv() throws ReflectiveOperationException {
        String path = "/a/sample/path/pgpass";
        // doesn't work on java 17+
        // updateEnv("PGPASSFILE", path);
        assertEquals(path, PgPass.getPgPassPath().toString());
    }

    /*
     * found at https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     */
    @SuppressWarnings({ "unchecked" })
    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }
}
