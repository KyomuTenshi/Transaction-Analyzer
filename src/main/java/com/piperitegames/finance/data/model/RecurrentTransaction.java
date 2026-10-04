package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Getter
@ToString(callSuper = true)
public class RecurrentTransaction extends Transaction {

    private final RecurrencePattern pattern;
    private final int repetitions;

    public RecurrentTransaction(long accountId, long id, LocalDateTime dateTime,
                                String category, BigDecimal amount,
                                RecurrencePattern pattern, int repetitions) {
        super(accountId, id, dateTime, category, amount);
        if (repetitions < 1) {
            throw new IllegalArgumentException("Количество повторений должно быть положительным: " + repetitions);
        }
        this.pattern = Objects.requireNonNull(pattern, "Паттерн повторения не может быть null");
        this.repetitions = repetitions;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.RECURRENT;
    }

    @Override
    public Stream<LocalDateTime> getOccurrences() {
        return LongStream.range(0, repetitions)
                .mapToObj(period -> pattern.shift(getDateTime(), period));
    }
}