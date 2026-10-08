package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
public class TaxableTransaction extends Transaction {

    private final BigDecimal taxRate;

    public TaxableTransaction(long accountId, long id, LocalDateTime dateTime,
                              String category, BigDecimal amount, BigDecimal taxRate) {
        super(accountId, id, dateTime, category, amount);
        Objects.requireNonNull(taxRate, "Ставка налога не может быть null");
        if (taxRate.signum() < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Ставка налога должна быть в диапазоне от 0 до 1: " + taxRate);
        }
        this.taxRate = taxRate;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TAXABLE;
    }

    @Override
    public BigDecimal getRealAmount() {
        return getAmount()
                .multiply(BigDecimal.ONE.subtract(taxRate))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}