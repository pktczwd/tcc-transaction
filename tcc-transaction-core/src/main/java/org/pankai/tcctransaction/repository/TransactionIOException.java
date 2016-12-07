package org.pankai.tcctransaction.repository;

/**
 * Created by pktczwd on 2016/12/7.
 */
public class TransactionIOException extends RuntimeException {

    public TransactionIOException(String message) {
        super(message);
    }

    public TransactionIOException(Throwable cause) {
        super(cause);
    }
}
