package com.piperitegames.finance.data.model;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class RegularTransaction extends Transaction {

    public RegularTransaction(long accountId, long id, LocalDateTime dateTime,
                              String category, BigDecimal amount) {
        super(accountId, id, dateTime, category, amount);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.REGULAR;
    }
}