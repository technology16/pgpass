[![Maven Central](https://img.shields.io/maven-central/v/com.github.technology16/PgPass.svg?label=Maven%20Central)](https://maven-badges.herokuapp.com/maven-central/com.github.technology16/PgPass)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### PgPass
Simple Java library for easy PostgreSQL [password files](https://www.postgresql.org/docs/current/static/libpq-pgpass.html) loading

#### Maven Repository

You can pull PgPass from the central maven repository, just add these to your pom.xml file:
```
<dependency>
    <groupId>com.github.technology16</groupId>
    <artifactId>PgPass</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Usage

API is simple. Currently static getters are available:
```
// Look at default location
PgPass.get(String host, String port, String dbName, String user)

// Look at provided location
PgPass.get(Path pgPassPath, String host, String port, String dbName, String user)

// Returns all PgPassEntry from default location
PgPass.getAll()

// Returns all PgPassEntry from provided location
PgPass.get(Path pgPassPath)

// Return pgpass file default location
PgPass.getPgPassPath()
```

#### License

This application is licensed under the Apache License, Version 2.0. See [LICENCE](LICENSE) for details.