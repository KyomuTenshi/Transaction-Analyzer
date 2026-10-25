package com.piperitegames.finance.exception;

public class AnalyticsNotCalculatedException extends FinanceAnalyzerException {

    public AnalyticsNotCalculatedException() {
        super("Необходимо сначала рассчитать аналитику");
    }
}