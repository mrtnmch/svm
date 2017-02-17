package cz.martinmach;

/**
 * Created by mmx on 17.2.17.
 */
public class UnknownDataFileException extends Exception {
    public UnknownDataFileException() {
        super();
    }

    public UnknownDataFileException(String message) {
        super(message);
    }
}
