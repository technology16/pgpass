package ru.taximaxim.PgPassLoader;

public class PgPassException extends Exception {

    public PgPassException(String format, Throwable e) {
        super(format, e);
    }
}
