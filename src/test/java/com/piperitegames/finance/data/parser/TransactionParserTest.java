package com.piperitegames.finance.data.parser;

import com.piperitegames.finance.data.model.CommentableTransaction;
import com.piperitegames.finance.data.model.ForeignCurrencyTransaction;
import com.piperitegames.finance.data.model.RecurrencePattern;
import com.piperitegames.finance.data.model.RecurrentTransaction;
import com.piperitegames.finance.data.model.RegularTransaction;
import com.piperitegames.finance.data.model.TaxableTransaction;
import com.piperitegames.finance.data.model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.piperitegames.finance.TestAssertions.assertAmount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionParserTest {

    private final TransactionParser parser = new TransactionParser();

    @Test
    void parsesRegularTransactionWithTrailingComma() {
        Transaction transaction = parser.parse("10001,2,2020-01-01T12:00:00,Food,-25.50,Regular,");

        assertInstanceOf(RegularTransaction.class, transaction);
        assertEquals(10001, transaction.getAccountId());
        assertEquals(2, transaction.getId());
        assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0), transaction.getDateTime());
        assertEquals("Food", transaction.getCategory());
        assertAmount("-25.50", transaction.getRealAmount());
    }

    @Test
    void subtractsTaxFromTaxableTransaction() {
        Transaction transaction = parser.parse("10002,8,2020-04-15T14:15:00,Salary,5000.00,Taxable,0.20");

        assertInstanceOf(TaxableTransaction.class, transaction);
        assertAmount("5000.00", transaction.getAmount());
        assertAmount("4000.00", transaction.getRealAmount());
    }

    @Test
    void convertsForeignCurrencyToBaseCurrency() {
        Transaction transaction = parser.parse("10003,12,2021-05-18T16:20:00,Entertainment,-75.99,ForeignCurrency,85");

        assertInstanceOf(ForeignCurrencyTransaction.class, transaction);
        assertAmount("-6459.15", transaction.getRealAmount());
    }

    @Test
    void expandsRecurrentTransactionOccurrences() {
        Transaction transaction = parser.parse("1534,6,2020-03-22T19:00:00,Entertainment,-45.00,Recurrent,monthly;7");

        RecurrentTransaction recurrent = assertInstanceOf(RecurrentTransaction.class, transaction);
        assertEquals(RecurrencePattern.MONTHLY, recurrent.getPattern());

        List<LocalDateTime> occurrences = recurrent.getOccurrences().toList();
        assertEquals(7, occurrences.size());
        assertEquals(LocalDateTime.of(2020, 3, 22, 19, 0), occurrences.get(0));
        assertEquals(LocalDateTime.of(2020, 9, 22, 19, 0), occurrences.get(6));
        assertAmount("-45.00", recurrent.getRealAmount());
    }

    @Test
    void keepsCommasInsideComments() {
        Transaction transaction = parser.parse("10002,26,2024-01-10T12:00:00,Food,-8.40,Commentable,Lunch, with colleagues;Paid by card");

        CommentableTransaction commentable = assertInstanceOf(CommentableTransaction.class, transaction);
        assertEquals(List.of("Lunch, with colleagues", "Paid by card"), commentable.getComments());
    }

    @Test
    void rejectsUnknownTransactionType() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("10001,2,2020-01-01T12:00:00,Food,-25.50,Bonus,"));
    }

    @Test
    void rejectsInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("10001,2,2020-01-01T12:00:00,Food,abc,Regular,"));
    }

    @Test
    void rejectsTaxableTransactionWithoutRate() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parse("10002,8,2020-04-15T14:15:00,Salary,5000.00,Taxable,"));
    }
}