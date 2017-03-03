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
 *
 * @author shaidullin_iz
 */
public class PgPassLoader {

    private static final String REGEX = "(?<=(?<!\\\\)):|(?<=(?<!\\\\)(\\\\){2}):|(?<=(?<!\\\\)(\\\\){4}):";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final String ANY = "*";

    private static final int HOST_IDX = 0;
    private static final int PORT_IDX = 1;
    private static final int NAME_IDX = 2;
    private static final int USER_IDX = 3;
    private static final int PASS_IDX = 4;

    private final String host;
    private final String port;
    private final String dbName;
    private final String user;

    public PgPassLoader(String host, String port, String dbName, String user) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
    }

    /**
     * Read password from default pgpass location
     */
    public char[] getPgPass() throws PgPassLoaderException {
        return getPgPass(getPgPassPath());
    }

    /**
     * Read password from pgpass located at {@code pgPassPath}
     *
     * @param pgPassPath path to pgpass file
     */
    public char[] getPgPass(Path pgPassPath) throws PgPassLoaderException {
        String pgPass = null;
        try (BufferedReader reader = Files.newBufferedReader(pgPassPath, StandardCharsets.UTF_8)) {
            String settingsLine;
            while ((settingsLine = reader.readLine()) != null) {
                if (!settingsLine.startsWith("#")){
                    String[] settings = PATTERN.split(settingsLine);
                    if (settingsMatch(settings)) {
                        pgPass = settings[PASS_IDX];
                        break;
                    }
                }
            }
        } catch (NoSuchFileException e) {
            throw new PgPassLoaderException(String.format("Файл pgpass не найден: %s", pgPassPath), e);
        } catch (IOException e) {
            throw new PgPassLoaderException(String.format("Ошибка чтения файла pgpass: %s", pgPassPath), e);
        }

        return pgPass != null ? pgPass.toCharArray() : new char[0];
    }

    private boolean settingsMatch(String[] settings) {
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
    private Path getPgPassPath() {
        Path path = Paths.get(System.getProperty("user.home")).resolve(Paths.get(".pgpass"));
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            path = Paths.get(System.getenv("APPDATA")).resolve(Paths.get("postgresql", "pgpass.conf"));
        }
        return path;
    }
}
