package com.piperitegames.finance.exception;

public class FinanceAnalyzerException extends RuntimeException {

    public FinanceAnalyzerException(String message) {
        super(message);
    }

    public FinanceAnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}