[![Maven Central](https://img.shields.io/maven-central/v/com.github.technology16/pgpass.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.technology16%22%20AND%20a:%22pgpass%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### PgPass
Simple Java library for easy PostgreSQL [password files](https://www.postgresql.org/docs/current/static/libpq-pgpass.html) loading

#### Maven Repository

You can pull PgPass from the central maven repository, just add these to your pom.xml file:
```
<dependency>
  <groupId>com.github.technology16</groupId>
  <artifactId>pgpass</artifactId>
  <version>2.0.0</version>
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

#### Docker Testing
```bash
docker-compose -f docker-compose.test.yml run --rm t1
```


#### License

This application is licensed under the Apache License, Version 2.0. See [LICENCE](LICENSE) for details.