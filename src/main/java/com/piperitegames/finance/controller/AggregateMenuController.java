package com.piperitegames.finance.controller;

import com.piperitegames.finance.controller.option.AggregateMenuOption;

public class AggregateMenuController extends AbstractMenuController<AggregateMenuOption> {

    private final AnalyticsSession session;

    public AggregateMenuController(ConsoleIO console, AnalyticsSession session) {
        super(console, "Выберите способ агрегации транзакций:", AggregateMenuOption.class);
        this.session = session;
    }

    @Override
    protected boolean handle(AggregateMenuOption option) {
        session.setAggregateOption(option.getAggregateOption());
        console.println("Выбрано: " + option.getAggregateOption().getTitle());
        return false;
    }
}