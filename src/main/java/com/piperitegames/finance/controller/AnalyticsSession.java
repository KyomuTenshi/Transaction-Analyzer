package com.piperitegames.finance.controller;

import com.piperitegames.finance.service.analytics.AggregateOption;
import com.piperitegames.finance.service.analytics.AnalyticsResult;
import com.piperitegames.finance.service.analytics.GroupOption;
import com.piperitegames.finance.service.filter.TransactionFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class AnalyticsSession {

    private TransactionFilter filter = TransactionFilter.empty();
    private GroupOption groupOption = GroupOption.NONE;
    private AggregateOption aggregateOption = AggregateOption.SUM;

    @Getter(AccessLevel.NONE)
    private AnalyticsResult lastResult;

    public Optional<AnalyticsResult> getLastResult() {
        return Optional.ofNullable(lastResult);
    }

    public String describe() {
        return "фильтр: %s; %s; агрегация: %s".formatted(
                filter.describe(), groupOption.getTitle(), aggregateOption.getTitle());
    }
}