package com.piperitegames.finance.service.filter;

import com.piperitegames.finance.data.model.Commentable;
import com.piperitegames.finance.data.model.Transaction;
import com.piperitegames.finance.exception.InvalidFilterException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionFilter {

    private static final TransactionFilter EMPTY =
            new TransactionFilter(null, null, null, null, null, null);

    private final String category;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final String commentPart;

    public static TransactionFilter empty() {
        return EMPTY;
    }

    public TransactionFilter withCategory(String category) {
        return new TransactionFilter(normalize(category), dateFrom, dateTo, minAmount, maxAmount, commentPart);
    }

    public TransactionFilter withDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidFilterException(
                    "Начальная дата %s не может быть позже конечной %s".formatted(from, to));
        }
        return new TransactionFilter(category, from, to, minAmount, maxAmount, commentPart);
    }

    public TransactionFilter withAmountRange(BigDecimal min, BigDecimal max) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new InvalidFilterException(
                    "Минимальная сумма %s не может быть больше максимальной %s"
                            .formatted(min.toPlainString(), max.toPlainString()));
        }
        return new TransactionFilter(category, dateFrom, dateTo, min, max, commentPart);
    }

    public TransactionFilter withComment(String commentPart) {
        return new TransactionFilter(category, dateFrom, dateTo, minAmount, maxAmount, normalize(commentPart));
    }

    public Predicate<Transaction> toPredicate() {
        return byCategory()
                .and(byDateRange())
                .and(byAmountRange())
                .and(byComment());
    }

    public boolean isEmpty() {
        return category == null && dateFrom == null && dateTo == null
                && minAmount == null && maxAmount == null && commentPart == null;
    }

    public String describe() {
        List<String> parts = new ArrayList<>();
        if (category != null) {
            parts.add("категория — " + category);
        }
        if (dateFrom != null) {
            parts.add("начальная дата — " + dateFrom);
        }
        if (dateTo != null) {
            parts.add("конечная дата — " + dateTo);
        }
        if (minAmount != null) {
            parts.add("минимальная сумма — " + minAmount.toPlainString());
        }
        if (maxAmount != null) {
            parts.add("максимальная сумма — " + maxAmount.toPlainString());
        }
        if (commentPart != null) {
            parts.add("комментарий содержит — " + commentPart);
        }
        return parts.isEmpty() ? "без фильтров" : String.join(", ", parts);
    }

    private Predicate<Transaction> byCategory() {
        if (category == null) {
            return transaction -> true;
        }
        return transaction -> transaction.getCategory().equals(category);
    }

    private Predicate<Transaction> byDateRange() {
        if (dateFrom == null && dateTo == null) {
            return transaction -> true;
        }
        LocalDateTime start = dateFrom == null ? LocalDateTime.MIN : dateFrom.atStartOfDay();
        LocalDateTime endExclusive = dateTo == null ? LocalDateTime.MAX : dateTo.plusDays(1).atStartOfDay();

        return transaction -> transaction.getOccurrences()
                .takeWhile(occurrence -> occurrence.isBefore(endExclusive))
                .anyMatch(occurrence -> !occurrence.isBefore(start));
    }

    private Predicate<Transaction> byAmountRange() {
        if (minAmount == null && maxAmount == null) {
            return transaction -> true;
        }
        return transaction -> {
            BigDecimal amount = transaction.getRealAmount();
            boolean aboveMin = minAmount == null || amount.compareTo(minAmount) >= 0;
            boolean belowMax = maxAmount == null || amount.compareTo(maxAmount) <= 0;
            return aboveMin && belowMax;
        };
    }

    private Predicate<Transaction> byComment() {
        if (commentPart == null) {
            return transaction -> true;
        }
        return transaction -> transaction instanceof Commentable commentable
                && commentable.hasCommentContaining(commentPart);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }
}