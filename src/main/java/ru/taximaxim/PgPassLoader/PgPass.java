package ru.taximaxim.PgPassLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Reads user password from pgpass file to be used to access certain DB passed to constructor
 */
public class PgPass {

    private static final String REGEX = "(?<=(?<!\\\\)):|(?<=(?<!\\\\)(\\\\){2}):|(?<=(?<!\\\\)(\\\\){4}):";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final String ANY = "*";

    private static final int HOST_IDX = 0;
    private static final int PORT_IDX = 1;
    private static final int NAME_IDX = 2;
    private static final int USER_IDX = 3;
    private static final int PASS_IDX = 4;

    /**
     * Read password from default pgpass location
     */
    public static String get(String host, String port, String dbName, String user) throws PgPassException {
        return get(getPgPassPath(), host, port, dbName, user);
    }

    /**
     * Read password from pgpass located at {@code pgPassPath}
     *
     * @param pgPassPath path to pgpass file
     */
    public static String get(Path pgPassPath, String host, String port, String dbName, String user)
            throws PgPassException {
        try (BufferedReader reader = Files.newBufferedReader(pgPassPath, StandardCharsets.UTF_8)) {
            String settingsLine;
            String pgPass = null;
            while ((settingsLine = reader.readLine()) != null) {
                if (!settingsLine.startsWith("#")){
                    String[] settings = PATTERN.split(settingsLine);
                    if (settingsMatch(settings, host, port, dbName, user)) {
                        pgPass = settings[PASS_IDX];
                        break;
                    }
                }
            }
            return pgPass;
        } catch (NoSuchFileException e) {
            throw new PgPassException(String.format("Pgpass file not found: %s", pgPassPath), e);
        } catch (IOException e) {
            throw new PgPassException(String.format("Failed reading pgpass file: %s", pgPassPath), e);
        }
    }

    private static boolean settingsMatch(String[] settings, String host, String port, String dbName, String user) {
        if (settings.length != 5) {
            return false;
        } else {
            boolean hostMatch = settings[HOST_IDX].equals(ANY) || settings[HOST_IDX].equals(host);
            boolean portMatch = settings[PORT_IDX].equals(ANY) || settings[PORT_IDX].equals(port);
            boolean nameMatch = settings[NAME_IDX].equals(ANY) || settings[NAME_IDX].equals(dbName);
            boolean userMatch = settings[USER_IDX].equals(ANY) || settings[USER_IDX].equals(user);
            return hostMatch && portMatch && nameMatch && userMatch;
        }
    }

    // TODO support PGPASSFILE
    private static Path getPgPassPath() {
        Path path = Paths.get(System.getProperty("user.home")).resolve(Paths.get(".pgpass"));
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            path = Paths.get(System.getenv("APPDATA")).resolve(Paths.get("postgresql", "pgpass.conf"));
        }
        return path;
    }
}
