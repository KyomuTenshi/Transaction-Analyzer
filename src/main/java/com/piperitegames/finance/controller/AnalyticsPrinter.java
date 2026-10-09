package com.piperitegames.finance.controller;

import com.piperitegames.finance.service.analytics.AnalyticsResult;

import java.time.format.DateTimeFormatter;

public class AnalyticsPrinter {

    private static final String DOUBLE_LINE = "===================================";
    private static final String SINGLE_LINE = "-----------------------------------";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ConsoleIO console;

    public AnalyticsPrinter(ConsoleIO console) {
        this.console = console;
    }

    public void print(AnalyticsResult result) {
        console.println(DOUBLE_LINE);
        console.println("Дата: " + result.calculatedAt().format(DATE_FORMAT));
        console.println("Расчёт: " + result.title());
        console.println("Фильтр: " + result.filter().describe());
        console.println(SINGLE_LINE);

        if (result.data().isEmpty()) {
            console.println("Нет транзакций, подходящих под заданные фильтры.");
        } else {
            console.println("Аналитика:");
            result.data().forEach((key, value) -> console.println("  " + key + ": " + value.toPlainString()));
        }
        console.println(DOUBLE_LINE);
    }
}