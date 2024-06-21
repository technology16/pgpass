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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Reads user password from pgpass file to be used to access certain DB passed to constructor
 */
public class PgPass {

    private static final String REGEX ="((?:[^:]|(?:\\\\:))+):((?:[^:]|(?:\\\\:))+):((?:[^:]|(?:\\\\:))+):((?:[^:]|(?:\\\\:))+):((?:[^:]|(?:\\\\:))+)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int HOST_IDX = 1;
    private static final int PORT_IDX = 2;
    private static final int NAME_IDX = 3;
    private static final int USER_IDX = 4;
    private static final int PASS_IDX = 5;

    /**
     * Read password from default pgpass location
     *
     * @param host - host name or '*' for any
     * @param port - host number or '*' for any
     * @param dbName - database name or '*' for any
     * @param user - user name or '*' for any
     *
     * @return first password by the given parameters, or null if there are no matches
     *
     * @throws PgPassException if file at default path doesn't exist or cannot be read
     */
    public static String get(String host, String port, String dbName, String user) throws PgPassException {
        return get(getPgPassPath(), host, port, dbName, user);
    }

    /**
     * Read password from pgpass located at {@code pgPassPath}
     *
     * @param pgPassPath - path to pgpass file
     * @param host - host name or '*' for any
     * @param port - host number or '*' for any
     * @param dbName - database name or '*' for any
     * @param user - user name or '*' for any
     *
     * @return first password by the given parameters, or null if there are no matches
     *
     * @throws PgPassException if file at given path doesn't exist or cannot be read
     */
    public static String get(Path pgPassPath, String host, String port, String dbName, String user)
            throws PgPassException {
        return getAll(pgPassPath).stream()
            .filter(e -> e.checkMatch(host, port, dbName, user))
            .map(PgPassEntry::getPass)
            .findAny()
            .orElse(null);
    }

    /**
     * Returns all PgPassEntry from default pgpass location
     *
     * @return all entries from default pgpass locations
     *
     * @throws PgPassException if file at given path doesn't exist or cannot be read
     */
    public static List<PgPassEntry> getAll() throws PgPassException {
        return getAll(getPgPassPath());
    }

    /**
     * Returns all PgPassEntry from pgpass located at {@code pgPassPath}
     *
     * @param pgPassPath path to pgpass file
     *
     * @return all entries from {@code pgPassPath}
     *
     * @throws PgPassException if file at given path doesn't exist or cannot be read
     */
    public static List<PgPassEntry> getAll(Path pgPassPath) throws PgPassException {
        try {
            return readLines(pgPassPath);
        } catch (NoSuchFileException e) {
            throw new PgPassException(String.format("Pgpass file not found: %s", pgPassPath), e);
        } catch (IOException e) {
            throw new PgPassException(String.format("Failed reading pgpass file: %s", pgPassPath), e);
        }
    }

    private static List<PgPassEntry> readLines(Path pgPassPath) throws IOException {
        List<PgPassEntry> entries = new ArrayList<>();
        for (String line : Files.readAllLines(pgPassPath)) {
            if (line.startsWith("#")) {
                continue;
            }

            Matcher pathParts = PATTERN.matcher(line);
            if (pathParts.matches() && pathParts.groupCount() == 5) {
                String host = unescape(pathParts.group(HOST_IDX));
                String port = unescape(pathParts.group(PORT_IDX));
                String dbName = unescape(pathParts.group(NAME_IDX));
                String user = unescape(pathParts.group(USER_IDX));
                String pass = unescape(pathParts.group(PASS_IDX));

                entries.add(new PgPassEntry(host, port, dbName, user, pass));
            }
        }
        return entries;
    }

    /**
     * Return pgpass default location as for standard postgreSQL cascade approach:
     * - check from env var: PGPASSFILE
     * - check for user home folder
     *
     * @return pgpass default location, null if not found
     */
    public static Path getPgPassPath() {
        String env = System.getenv("PGPASSFILE");
        if (env != null) {
            return Paths.get(env);
        }

        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return Paths.get(System.getenv("APPDATA"), "postgresql", "pgpass.conf");
        }

        return Paths.get(System.getProperty("user.home"), ".pgpass");
    }

    /**
     * Removes escape characters from the given string
     *
     * @param line string with escape characters
     *
     * @return string without escape characters
     */
    public static String unescape(String line) {
        StringBuilder newLine = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '\\') {
                newLine.append(line.charAt(i));
            } else if (i + 1 < line.length()) {
                switch (line.charAt(i + 1)) {
                case ':':
                case '\\':
                    newLine.append(line.charAt(++i));
                    break;
                default:
                    break;
                }
            }
        }
        return newLine.toString();
    }

    private PgPass() {}
}
