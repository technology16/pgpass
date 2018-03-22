package ru.taximaxim.pgpass;

public class PgPassException extends Exception {

    private static final long serialVersionUID = 761311897034050960L;

    public PgPassException(String format, Throwable e) {
        super(format, e);
    }
}
