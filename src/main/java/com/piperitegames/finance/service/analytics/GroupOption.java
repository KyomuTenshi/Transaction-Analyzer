package com.piperitegames.finance.service.analytics;

import com.piperitegames.finance.data.model.Account;
import com.piperitegames.finance.data.model.Transaction;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public enum GroupOption {
    NONE("без группировки", GroupOption::byTransaction),
    MONTH("группировка по месяцам", GroupOption::byMonth),
    YEAR("группировка по годам", GroupOption::byYear),
    DAY_OF_WEEK("группировка по дню недели", GroupOption::byDayOfWeek),
    CATEGORY("группировка по категории", GroupOption::byCategory),
    INCOME_EXPENSE("доходы и расходы", GroupOption::byIncomeExpense),
    ACCOUNT_TYPE("группировка по типу счёта", GroupOption::byAccountType),
    USER_ID("группировка по ID пользователя", GroupOption::byUserId);

    private static final Locale RUSSIAN = Locale.forLanguageTag("ru");
    private static final GroupKey INCOME = new GroupKey(0, "доходы");
    private static final GroupKey EXPENSE = new GroupKey(1, "расходы");
    private static final GroupKey ZERO = new GroupKey(2, "нулевые");

    @Getter
    private final String title;
    private final Classifier classifier;

    GroupOption(String title, Classifier classifier) {
        this.title = title;
        this.classifier = classifier;
    }

    public GroupKey classify(Transaction transaction, AccountLookup accounts) {
        return classifier.classify(transaction, accounts);
    }

    @FunctionalInterface
    private interface Classifier {
        GroupKey classify(Transaction transaction, AccountLookup accounts);
    }

    private static GroupKey byTransaction(Transaction transaction, AccountLookup accounts) {
        return new GroupKey(transaction.getId(), String.valueOf(transaction.getId()));
    }

    private static GroupKey byMonth(Transaction transaction, AccountLookup accounts) {
        YearMonth month = YearMonth.from(transaction.getDateTime());
        return new GroupKey(month.getYear() * 100L + month.getMonthValue(), month.toString());
    }

    private static GroupKey byYear(Transaction transaction, AccountLookup accounts) {
        int year = transaction.getDateTime().getYear();
        return new GroupKey(year, String.valueOf(year));
    }

    private static GroupKey byDayOfWeek(Transaction transaction, AccountLookup accounts) {
        DayOfWeek day = transaction.getDateTime().getDayOfWeek();
        return new GroupKey(day.getValue(), day.getDisplayName(TextStyle.FULL_STANDALONE, RUSSIAN));
    }

    private static GroupKey byCategory(Transaction transaction, AccountLookup accounts) {
        return new GroupKey(0, transaction.getCategory());
    }

    private static GroupKey byIncomeExpense(Transaction transaction, AccountLookup accounts) {
        int sign = transaction.getRealAmount().signum();
        if (sign > 0) {
            return INCOME;
        }
        return sign < 0 ? EXPENSE : ZERO;
    }

    private static GroupKey byAccountType(Transaction transaction, AccountLookup accounts) {
        return accounts.find(transaction.getAccountId())
                .map(Account::getType)
                .map(type -> new GroupKey(type.getCode(), type.getTitle()))
                .orElse(GroupKey.UNKNOWN_ACCOUNT);
    }

    private static GroupKey byUserId(Transaction transaction, AccountLookup accounts) {
        return accounts.find(transaction.getAccountId())
                .map(Account::getUserId)
                .map(userId -> new GroupKey(userId, "пользователь " + userId))
                .orElse(GroupKey.UNKNOWN_ACCOUNT);
    }
}