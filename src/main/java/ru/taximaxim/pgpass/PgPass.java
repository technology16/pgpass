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

    private static final String REGEX ="((?:[^:]|(?:\\:))+):((?:[^:]|(?:\\:))+):((?:[^:]|(?:\\:))+):((?:[^:]|(?:\\:))+):((?:[^:]|(?:\\:))+)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final int HOST_IDX = 1;
    private static final int PORT_IDX = 2;
    private static final int NAME_IDX = 3;
    private static final int USER_IDX = 4;
    private static final int PASS_IDX = 5;

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
        return getAll(pgPassPath).stream().filter(e -> e.checkMatch(host, port, dbName, user))
                .map(PgPassEntry::getPass).findAny().orElse(null);
    }

    /**
     * Returns all PgPassEntry from default pgpass location
     */
    public static List<PgPassEntry> getAll() throws PgPassException {
    	return getAll(getPgPassPath());
    }

    /**
     * Returns all PgPassEntry from pgpass located at {@code pgPassPath}
     *
     * @param pgPassPath path to pgpass file
     */
    public static List<PgPassEntry> getAll(Path pgPassPath) throws PgPassException {
        try {
			List<PgPassEntry> allPassPath = new ArrayList<PgPassEntry>();
			for (String line : Files.readAllLines(pgPassPath)) {
				if (!line.startsWith("#")) {
					Matcher pathParts = PATTERN.matcher(line);
					if (pathParts.find()) {
						if (pathParts.groupCount() == 5) {
							allPassPath.add(new PgPassEntry(pathParts.group(HOST_IDX), pathParts.group(PORT_IDX),
									pathParts.group(NAME_IDX), pathParts.group(USER_IDX), pathParts.group(PASS_IDX)));
						}
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
     * Return pgpass default location
     */
    public static Path getPgPassPath() {
        Path path = Paths.get(System.getProperty("user.home")).resolve(Paths.get(".pgpass"));
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            path = Paths.get(System.getenv("APPDATA")).resolve(Paths.get("postgresql", "pgpass.conf"));
        }
        return path;
    }

    private PgPass() {

    }
}
