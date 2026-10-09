package com.piperitegames.finance.service;

import com.piperitegames.finance.data.repository.AccountRepository;
import com.piperitegames.finance.data.repository.TransactionRepository;
import com.piperitegames.finance.service.analytics.AggregateOption;
import com.piperitegames.finance.service.analytics.AnalyticsResult;
import com.piperitegames.finance.service.analytics.GroupKey;
import com.piperitegames.finance.service.analytics.GroupOption;
import com.piperitegames.finance.service.filter.TransactionFilter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DefaultTransactionService implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final Clock clock;

    public DefaultTransactionService(TransactionRepository transactionRepository,
                                     AccountRepository accountRepository) {
        this(transactionRepository, accountRepository, Clock.systemDefaultZone());
    }

    public DefaultTransactionService(TransactionRepository transactionRepository,
                                     AccountRepository accountRepository,
                                     Clock clock) {
        this.transactionRepository = Objects.requireNonNull(transactionRepository);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public AnalyticsResult calculate(TransactionFilter filter,
                                     GroupOption groupOption,
                                     AggregateOption aggregateOption) {
        Objects.requireNonNull(filter, "Фильтр не может быть null");
        Objects.requireNonNull(groupOption, "Опция группировки не может быть null");
        Objects.requireNonNull(aggregateOption, "Опция агрегации не может быть null");

        Map<GroupKey, BigDecimal> grouped = transactionRepository.findAll().stream()
                .filter(filter.toPredicate())
                .collect(Collectors.groupingBy(
                        transaction -> groupOption.classify(transaction, accountRepository::findById),
                        TreeMap::new,
                        aggregateOption.getCollector()));

        Map<String, BigDecimal> data = new LinkedHashMap<>();
        grouped.forEach((key, value) -> data.put(key.label(), value));

        LocalDateTime calculatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        return new AnalyticsResult(calculatedAt, filter, groupOption, aggregateOption, data);
    }
}