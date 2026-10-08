package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
public class ForeignCurrencyTransaction extends Transaction {

    private final BigDecimal exchangeRate;

    public ForeignCurrencyTransaction(long accountId, long id, LocalDateTime dateTime,
                                      String category, BigDecimal amount, BigDecimal exchangeRate) {
        super(accountId, id, dateTime, category, amount);
        Objects.requireNonNull(exchangeRate, "Курс валюты не может быть null");
        if (exchangeRate.signum() <= 0) {
            throw new IllegalArgumentException("Курс валюты должен быть положительным: " + exchangeRate);
        }
        this.exchangeRate = exchangeRate;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.FOREIGN_CURRENCY;
    }

    @Override
    public BigDecimal getRealAmount() {
        return getAmount()
                .multiply(exchangeRate)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}