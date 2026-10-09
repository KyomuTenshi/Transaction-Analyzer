package com.piperitegames.finance.service.filter;

import com.piperitegames.finance.data.model.CommentableTransaction;
import com.piperitegames.finance.data.model.RecurrencePattern;
import com.piperitegames.finance.data.model.RecurrentTransaction;
import com.piperitegames.finance.data.model.RegularTransaction;
import com.piperitegames.finance.data.model.TaxableTransaction;
import com.piperitegames.finance.data.model.Transaction;
import com.piperitegames.finance.exception.InvalidFilterException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionFilterTest {

    private static final Transaction FOOD = new RegularTransaction(
            1, 1, LocalDateTime.of(2023, 1, 15, 10, 0), "Food", new BigDecimal("-20.00"));

    private static final Transaction YEARLY_INSURANCE = new RecurrentTransaction(
            1, 2, LocalDateTime.of(2015, 12, 31, 10, 0), "Insurance", new BigDecimal("-120.00"),
            RecurrencePattern.YEARLY, 10);

    private static final Transaction SALARY = new TaxableTransaction(
            1, 3, LocalDateTime.of(2020, 4, 15, 14, 0), "Salary", new BigDecimal("5000.00"),
            new BigDecimal("0.20"));

    private static final Transaction THEATER = new CommentableTransaction(
            1, 4, LocalDateTime.of(2023, 7, 15, 18, 30), "Leisure", new BigDecimal("-60.00"),
            List.of("Theater tickets", "HelloWorld"));

    @Test
    void emptyFilterAcceptsEverything() {
        TransactionFilter filter = TransactionFilter.empty();

        assertTrue(filter.isEmpty());
        assertTrue(filter.toPredicate().test(FOOD));
        assertTrue(filter.toPredicate().test(YEARLY_INSURANCE));
    }

    @Test
    void categoryFilterIsCaseSensitive() {
        assertTrue(TransactionFilter.empty().withCategory("Food").toPredicate().test(FOOD));
        assertFalse(TransactionFilter.empty().withCategory("food").toPredicate().test(FOOD));
    }

    @Test
    void blankCategoryDisablesFilter() {
        TransactionFilter filter = TransactionFilter.empty().withCategory("   ");

        assertTrue(filter.isEmpty());
    }

    @Test
    void dateRangeIncludesWholeEndDay() {
        TransactionFilter filter = TransactionFilter.empty()
                .withDateRange(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 15));

        assertTrue(filter.toPredicate().test(FOOD));
    }

    @Test
    void dateRangeMatchesRecurrentTransactionOccurrence() {
        TransactionFilter filter = TransactionFilter.empty()
                .withDateRange(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 2));

        assertTrue(filter.toPredicate().test(YEARLY_INSURANCE));
    }

    @Test
    void dateRangeAfterLastOccurrenceDoesNotMatch() {
        TransactionFilter filter = TransactionFilter.empty()
                .withDateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 12, 31));

        assertFalse(filter.toPredicate().test(YEARLY_INSURANCE));
    }

    @Test
    void amountRangeUsesAmountAfterTax() {
        TransactionFilter upTo4500 = TransactionFilter.empty().withAmountRange(null, new BigDecimal("4500"));
        TransactionFilter from4500 = TransactionFilter.empty().withAmountRange(new BigDecimal("4500"), null);

        assertTrue(upTo4500.toPredicate().test(SALARY));
        assertFalse(from4500.toPredicate().test(SALARY));
    }

    @Test
    void commentFilterIsCaseInsensitiveSubstringSearch() {
        assertTrue(TransactionFilter.empty().withComment("he").toPredicate().test(THEATER));
        assertTrue(TransactionFilter.empty().withComment("WORLD").toPredicate().test(THEATER));
        assertFalse(TransactionFilter.empty().withComment("book").toPredicate().test(THEATER));
    }

    @Test
    void commentFilterExcludesNonCommentableTransactions() {
        assertFalse(TransactionFilter.empty().withComment("food").toPredicate().test(FOOD));
    }

    @Test
    void rejectsReversedDateRange() {
        assertThrows(InvalidFilterException.class, () -> TransactionFilter.empty()
                .withDateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2024, 1, 1)));
    }

    @Test
    void rejectsReversedAmountRange() {
        assertThrows(InvalidFilterException.class, () -> TransactionFilter.empty()
                .withAmountRange(new BigDecimal("100"), new BigDecimal("-100")));
    }
}