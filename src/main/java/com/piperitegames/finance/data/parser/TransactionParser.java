package com.piperitegames.finance.data.parser;

import com.piperitegames.finance.data.model.CommentableTransaction;
import com.piperitegames.finance.data.model.ForeignCurrencyTransaction;
import com.piperitegames.finance.data.model.RecurrencePattern;
import com.piperitegames.finance.data.model.RecurrentTransaction;
import com.piperitegames.finance.data.model.RegularTransaction;
import com.piperitegames.finance.data.model.TaxableTransaction;
import com.piperitegames.finance.data.model.Transaction;
import com.piperitegames.finance.data.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TransactionParser implements LineParser<Transaction> {

    private static final String FIELD_SEPARATOR = ",";
    private static final String EXTRA_SEPARATOR = ";";
    private static final int MIN_FIELDS = 6;
    private static final int MAX_FIELDS = 7;

    @Override
    public Transaction parse(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, MAX_FIELDS);
        if (fields.length < MIN_FIELDS) {
            throw new IllegalArgumentException(
                    "ожидается не менее %d полей, получено %d".formatted(MIN_FIELDS, fields.length));
        }

        long accountId = ParsingSupport.parseLong(fields[0], "ID счёта");
        long id = ParsingSupport.parseLong(fields[1], "ID транзакции");
        LocalDateTime dateTime = ParsingSupport.parseDateTime(fields[2], "дата транзакции");
        String category = ParsingSupport.requireNotBlank(fields[3], "категория");
        BigDecimal amount = ParsingSupport.parseDecimal(fields[4], "сумма");
        String typeCode = fields[5].strip();
        TransactionType type = TransactionType.fromCode(typeCode)
                .orElseThrow(() -> new IllegalArgumentException("неизвестный тип транзакции: '" + typeCode + "'"));
        List<String> extra = fields.length == MAX_FIELDS ? splitExtra(fields[6]) : List.of();

        return switch (type) {
            case REGULAR -> new RegularTransaction(accountId, id, dateTime, category, amount);
            case TAXABLE -> new TaxableTransaction(accountId, id, dateTime, category, amount,
                    ParsingSupport.parseDecimal(single(extra, "ставка налога"), "ставка налога"));
            case FOREIGN_CURRENCY -> new ForeignCurrencyTransaction(accountId, id, dateTime, category, amount,
                    ParsingSupport.parseDecimal(single(extra, "курс валюты"), "курс валюты"));
            case RECURRENT -> {
                requireCount(extra, 2, "паттерн и количество повторений");
                String patternCode = extra.get(0);
                RecurrencePattern pattern = RecurrencePattern.fromCode(patternCode)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "неизвестный паттерн повторения: '" + patternCode + "'"));
                int repetitions = ParsingSupport.parseInt(extra.get(1), "количество повторений");
                yield new RecurrentTransaction(accountId, id, dateTime, category, amount, pattern, repetitions);
            }
            case COMMENTABLE -> new CommentableTransaction(accountId, id, dateTime, category, amount, extra);
        };
    }

    private static List<String> splitExtra(String raw) {
        return Arrays.stream(raw.split(EXTRA_SEPARATOR))
                .map(String::strip)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    private static String single(List<String> values, String fieldName) {
        requireCount(values, 1, fieldName);
        return values.get(0);
    }

    private static void requireCount(List<String> values, int expected, String fieldName) {
        if (values.size() != expected) {
            throw new IllegalArgumentException("для поля «%s» ожидается значений: %d, получено: %d"
                    .formatted(fieldName, expected, values.size()));
        }
    }
}