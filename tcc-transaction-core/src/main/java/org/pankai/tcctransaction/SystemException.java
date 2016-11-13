package org.pankai.tcctransaction;

/**
 * Created by pankai on 2016/11/13.
 */
public class SystemException extends RuntimeException {


    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
