### PgPass
Simple Java library for easy PostgreSQL [password files](https://www.postgresql.org/docs/current/static/libpq-pgpass.html) loading

#### Installation

Install this lib to local Maven repository using command (replace `<path-to-file>` by actual jar path):
```
mvn install:install-file
   -Dfile=<path-to-file>
   -DgroupId=ru.taximaxim
   -DartifactId=PgPass
   -Dversion=0.0.1
   -Dpackaging=jar
   -DgeneratePom=true
```

#### Usage

API is simple. Currently two static getters are available:
```
// Look at default location
PgPass.get(String host, String port, String dbName, String user)

// Look at provided location
PgPass.get(Path pgPassPath, String host, String port, String dbName, String user)
```
