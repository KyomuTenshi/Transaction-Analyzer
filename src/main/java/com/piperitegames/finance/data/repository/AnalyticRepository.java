package com.piperitegames.finance.data.repository;

import com.piperitegames.finance.data.model.AnalyticsReport;

public interface AnalyticRepository {

    void save(AnalyticsReport report);
}