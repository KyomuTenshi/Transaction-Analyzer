package com.piperitegames.finance.service;

import com.piperitegames.finance.data.model.AnalyticsReport;
import com.piperitegames.finance.data.repository.AnalyticRepository;
import com.piperitegames.finance.service.analytics.AnalyticsResult;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class DefaultReportService implements ReportService {

    private final AnalyticRepository analyticRepository;

    @Override
    public void save(AnalyticsResult result) {
        Objects.requireNonNull(result, "Результат аналитики не может быть null");

        AnalyticsReport report = AnalyticsReport.builder()
                .date(result.calculatedAt())
                .groupOption(result.groupOption().getTitle())
                .aggregateOption(result.aggregateOption().getTitle())
                .filter(result.filter().describe())
                .data(result.data())
                .build();

        analyticRepository.save(report);
    }
}