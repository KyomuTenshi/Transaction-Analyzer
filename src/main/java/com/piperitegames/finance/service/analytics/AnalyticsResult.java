package com.piperitegames.finance.service.analytics;

import com.piperitegames.finance.service.filter.TransactionFilter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record AnalyticsResult(
        LocalDateTime calculatedAt,
        TransactionFilter filter,
        GroupOption groupOption,
        AggregateOption aggregateOption,
        Map<String, BigDecimal> data
) {

    public AnalyticsResult {
        Objects.requireNonNull(calculatedAt, "Время расчёта не может быть null");
        Objects.requireNonNull(filter, "Фильтр не может быть null");
        Objects.requireNonNull(groupOption, "Опция группировки не может быть null");
        Objects.requireNonNull(aggregateOption, "Опция агрегации не может быть null");
        data = Collections.unmodifiableMap(new LinkedHashMap<>(data));
    }

    public String title() {
        return "%s (%s)".formatted(groupOption.getTitle(), aggregateOption.getTitle());
    }
}