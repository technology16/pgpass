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
     * Compare entry parameters with given parameters
     *
     * @param host - host name or '*' for any
     * @param port - host number or '*' for any
     * @param dbName - database name or '*' for any
     * @param user - user name or '*' for any
     *
     * @return true if parameters are equals or false otherwise
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
        return Objects.hash(host, port, dbName, user, pass);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof PgPassEntry) {
            PgPassEntry entry = (PgPassEntry) obj;

            return Objects.equals(host, entry.getHost())
                    && Objects.equals(port, entry.getPort())
                    && Objects.equals(dbName, entry.getDbName())
                    && Objects.equals(user, entry.getUser())
                    && Objects.equals(pass, entry.getPass());
        }
        return false;
    }
}
