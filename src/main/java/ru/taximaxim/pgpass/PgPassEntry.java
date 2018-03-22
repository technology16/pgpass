package ru.taximaxim.pgpass;

import java.util.Objects;

/**
 * Container for pgpass line params
 *
 * @author galiev_mr
 */
public class PgPassEntry {
    private static final String ANY = "*";

    private final String host;
    private final String port;
    private final String dbName;
    private final String user;
    private final String pass;

    public PgPassEntry(String host, String port, String dbName, String user, String pass) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.pass = pass;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }


    /**
     * Compare entry params with given params
     */
    public boolean checkMatch(String host, String port, String dbName, String user) {
        boolean hostMatch = ANY.equals(getHost()) || getHost().equals(host);
        boolean portMatch = ANY.equals(getPort()) || getPort().equals(port);
        boolean nameMatch = ANY.equals(getDbName()) || getDbName().equals(dbName);
        boolean userMatch = ANY.equals(getUser()) || getUser().equals(user);
        return hostMatch && portMatch && nameMatch && userMatch;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((pass == null) ? 0 : pass.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean eq = false;

        if (this == obj) {
            eq = true;
        } else if (obj instanceof PgPassEntry) {
            PgPassEntry entry = (PgPassEntry) obj;

            eq = Objects.equals(host, entry.getHost())
                    && Objects.equals(port, entry.getPort())
                    && Objects.equals(dbName, entry.getDbName())
                    && Objects.equals(user, entry.getUser())
                    && Objects.equals(pass, entry.getPass());
        }
        return eq;
    }
}
