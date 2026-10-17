package com.piperitegames.finance.service.analytics;

import com.piperitegames.finance.data.model.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum AggregateOption {
    SUM("подсчёт суммы",
            Collectors.reducing(BigDecimal.ZERO, Transaction::getRealAmount, BigDecimal::add)),

    AVERAGE("подсчёт среднего значения",
            Collectors.teeing(
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getRealAmount, BigDecimal::add),
                    Collectors.counting(),
                    (sum, count) -> sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL64))),

    COUNT("подсчёт количества",
            Collectors.collectingAndThen(Collectors.counting(), BigDecimal::valueOf));

    private final String title;
    private final Collector<Transaction, ?, BigDecimal> collector;
}