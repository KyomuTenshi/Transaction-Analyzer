package com.piperitegames.finance.controller;

import com.piperitegames.finance.controller.option.MainMenuOption;
import com.piperitegames.finance.exception.AnalyticsNotCalculatedException;
import com.piperitegames.finance.service.ReportService;
import com.piperitegames.finance.service.TransactionService;
import com.piperitegames.finance.service.analytics.AnalyticsResult;

import java.util.Optional;

public class MainMenuController extends AbstractMenuController<MainMenuOption> {

    private final AnalyticsSession session;
    private final TransactionService transactionService;
    private final ReportService reportService;
    private final AnalyticsPrinter printer;
    private final SearchMenuController searchMenu;
    private final GroupMenuController groupMenu;
    private final AggregateMenuController aggregateMenu;

    public MainMenuController(ConsoleIO console,
                              AnalyticsSession session,
                              TransactionService transactionService,
                              ReportService reportService) {
        super(console, "Анализ финансов", MainMenuOption.class);
        this.session = session;
        this.transactionService = transactionService;
        this.reportService = reportService;
        this.printer = new AnalyticsPrinter(console);
        this.searchMenu = new SearchMenuController(console, session);
        this.groupMenu = new GroupMenuController(console, session);
        this.aggregateMenu = new AggregateMenuController(console, session);
    }

    @Override
    protected Optional<String> header() {
        return Optional.of("Текущие настройки — " + session.describe());
    }

    @Override
    protected boolean handle(MainMenuOption option) {
        switch (option) {
            case EXIT -> {
                console.println("До свидания!");
                return false;
            }
            case SEARCH -> searchMenu.run();
            case GROUP -> groupMenu.run();
            case AGGREGATE -> aggregateMenu.run();
            case CALCULATE -> calculate();
            case SAVE -> save();
        }
        return true;
    }

    private void calculate() {
        AnalyticsResult result = transactionService.calculate(
                session.getFilter(), session.getGroupOption(), session.getAggregateOption());
        session.setLastResult(result);
        printer.print(result);
    }

    private void save() {
        AnalyticsResult result = session.getLastResult()
                .orElseThrow(AnalyticsNotCalculatedException::new);
        reportService.save(result);
        console.println("Аналитика сохранена: " + result.title());
    }
}