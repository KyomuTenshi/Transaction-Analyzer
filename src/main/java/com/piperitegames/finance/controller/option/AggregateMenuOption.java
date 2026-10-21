package com.piperitegames.finance.controller.option;

import com.piperitegames.finance.service.analytics.AggregateOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AggregateMenuOption implements MenuOption {
    SUM(0, "подсчёт суммы", AggregateOption.SUM),
    AVERAGE(1, "подсчёт среднего значения", AggregateOption.AVERAGE),
    COUNT(2, "подсчёт количества", AggregateOption.COUNT);

    private final int code;
    private final String text;
    private final AggregateOption aggregateOption;
}