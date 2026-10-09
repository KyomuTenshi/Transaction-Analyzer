package com.piperitegames.finance.service;

import com.piperitegames.finance.service.analytics.AggregateOption;
import com.piperitegames.finance.service.analytics.AnalyticsResult;
import com.piperitegames.finance.service.analytics.GroupOption;
import com.piperitegames.finance.service.filter.TransactionFilter;

public interface TransactionService {

    AnalyticsResult calculate(TransactionFilter filter, GroupOption groupOption, AggregateOption aggregateOption);
}