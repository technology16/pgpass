/*
 * ========================LICENSE_START=================================
 * PgPass
 * *
 * Copyright (C) 2017-2020 "Technology" LLC
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package ru.taximaxim.pgpass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        return getAll(pgPassPath).stream().filter(e -> e.checkMatch(host, port, dbName, user))
                .map(PgPassEntry::getPass).findAny().orElse(null);
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
            List<PgPassEntry> allPassPath = new ArrayList<>();
            for (String line : Files.readAllLines(pgPassPath)) {
                if (!line.startsWith("#")) {
                    Matcher pathParts = PATTERN.matcher(line);
                    if (pathParts.matches() && pathParts.groupCount() == 5) {
                        allPassPath.add(new PgPassEntry(unescape(pathParts.group(HOST_IDX)),
                                unescape(pathParts.group(PORT_IDX)), unescape(pathParts.group(NAME_IDX)),
                                unescape(pathParts.group(USER_IDX)), unescape(pathParts.group(PASS_IDX))));
                    }
                }
            }
            return allPassPath;

        } catch (NoSuchFileException e) {
            throw new PgPassException(String.format("Pgpass file not found: %s", pgPassPath), e);
        } catch (IOException e) {
            throw new PgPassException(String.format("Failed reading pgpass file: %s", pgPassPath), e);
        }
    }

    /**
     * Return pgpass default location as for standard postgreSQL cascade approach:
     * - check from env var: PGPASSFILE
     * - check for user home folder
     *
     * @return pgpass default location, null if not found
     */
    public static Path getPgPassPath() {
        Path path;
        String OS = System.getProperty("os.name").toLowerCase();
        if (System.getenv().containsKey("PGPASSFILE")) {
            path = Paths.get(System.getenv("PGPASSFILE"));
        } else if (OS.contains("win")) {
            path = Paths.get(System.getProperty("APPDATA")).resolve(Paths.get("postgresql", "pgpass.conf"));
        } else {
            path = Paths.get(System.getProperty("user.home")).resolve(Paths.get(".pgpass"));
        }
        return path;
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

            if (line.charAt(i) == '\\') {
                if (i+1 < line.length()) {
                    switch (line.charAt(i + 1) ) {
                    case ':':
                    case '\\':
                        newLine.append(line.charAt(i + 1));
                        i++;
                        break;
                    default:
                        break;
                    }
                }
            } else {
                newLine.append(line.charAt(i));
            }
        }
        return newLine.toString();
    }

    private PgPass() {

    }
}
