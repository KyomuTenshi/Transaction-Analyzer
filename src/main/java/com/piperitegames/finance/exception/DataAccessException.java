package com.piperitegames.finance.exception;

public class DataAccessException extends FinanceAnalyzerException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}