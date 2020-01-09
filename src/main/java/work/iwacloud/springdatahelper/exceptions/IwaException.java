package work.iwacloud.springdatahelper.exceptions;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 21/12/2019 - 23:29
 */

/**
 * A custom exception to this helper
 */
@SuppressWarnings("all")
public class IwaException extends Exception {

    public IwaException() {
        super();
    }

    public IwaException(String message) {
        super(message);
    }

    public IwaException(String message, Throwable cause) {
        super(message, cause);
    }

    public IwaException(Throwable cause) {
        super(cause);
    }

    protected IwaException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
