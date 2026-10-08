package com.piperitegames.finance.data.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class AnalyticsReport {
    @NonNull LocalDateTime date;
    @NonNull String groupOption;
    @NonNull String aggregateOption;
    @NonNull String filter;
    @NonNull Map<String, BigDecimal> data;
}