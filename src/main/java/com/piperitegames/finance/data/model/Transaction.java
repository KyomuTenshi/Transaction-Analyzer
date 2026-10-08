package com.piperitegames.finance.data.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Transaction {

    protected static final int MONEY_SCALE = 2;

    private final long accountId;
    @EqualsAndHashCode.Include
    private final long id;
    private final LocalDateTime dateTime;
    private final String category;
    private final BigDecimal amount;

    protected Transaction(long accountId, long id, LocalDateTime dateTime,
                          String category, BigDecimal amount) {
        this.accountId = accountId;
        this.id = id;
        this.dateTime = Objects.requireNonNull(dateTime, "Дата транзакции не может быть null");
        this.category = Objects.requireNonNull(category, "Категория транзакции не может быть null");
        this.amount = Objects.requireNonNull(amount, "Сумма транзакции не может быть null");
    }

    public abstract TransactionType getType();

    /**
     * Сумма в базовой валюте с учётом налогов, курса и т. п.
     */
    public BigDecimal getRealAmount() {
        return amount;
    }

    /**
     * Все даты выполнения транзакции. У обычной транзакции она одна.
     */
    public Stream<LocalDateTime> getOccurrences() {
        return Stream.of(dateTime);
    }
}