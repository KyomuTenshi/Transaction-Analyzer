package com.piperitegames.finance.service;

import com.piperitegames.finance.data.model.Account;
import com.piperitegames.finance.data.model.AccountType;
import com.piperitegames.finance.data.model.Transaction;
import com.piperitegames.finance.data.parser.TransactionParser;
import com.piperitegames.finance.data.repository.AccountRepository;
import com.piperitegames.finance.data.repository.TransactionRepository;
import com.piperitegames.finance.service.analytics.AggregateOption;
import com.piperitegames.finance.service.analytics.AnalyticsResult;
import com.piperitegames.finance.service.analytics.GroupOption;
import com.piperitegames.finance.service.filter.TransactionFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.piperitegames.finance.TestAssertions.assertAmount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultTransactionServiceTest {

    private static final List<String> SPECIFICATION_DATA = List.of(
            "10001,2,2020-01-01T12:00:00,Food,-25.50,Regular,",
            "10001,4,2020-01-15T08:30:00,Transport,-15.00,Regular,",
            "10001,6,2020-03-22T19:00:00,Entertainment,-45.00,ForeignCurrency,85",
            "10002,8,2020-04-15T14:15:00,Salary,5000.00,Taxable,0.20",
            "10002,10,2021-05-10T11:00:00,Salary,100.00,Taxable,0.10",
            "10003,12,2021-05-18T16:20:00,Entertainment,-75.99,ForeignCurrency,85",
            "10003,14,2022-03-05T07:45:00,Food,-12.50,Regular,"
    );

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-09T12:00:00.123Z"), ZoneOffset.UTC);

    private TransactionService service;

    @BeforeEach
    void setUp() {
        TransactionParser parser = new TransactionParser();
        List<Transaction> transactions = SPECIFICATION_DATA.stream().map(parser::parse).toList();

        TransactionRepository transactionRepository = () -> transactions;
        AccountRepository accountRepository = accounts(
                new Account(10001, AccountType.CURRENT, 1),
                new Account(10002, AccountType.SAVINGS, 1),
                new Account(10003, AccountType.CREDIT, 2));

        service = new DefaultTransactionService(transactionRepository, accountRepository, FIXED_CLOCK);
    }

    @Test
    void sumsByYear() {
        Map<String, BigDecimal> data = calculate(GroupOption.YEAR, AggregateOption.SUM).data();

        assertEquals(List.of("2020", "2021", "2022"), List.copyOf(data.keySet()));
        assertAmount("134.50", data.get("2020"));
        assertAmount("-6369.15", data.get("2021"));
        assertAmount("-12.50", data.get("2022"));
    }

    @Test
    void averagesByYearWithoutRounding() {
        Map<String, BigDecimal> data = calculate(GroupOption.YEAR, AggregateOption.AVERAGE).data();

        assertAmount("33.625", data.get("2020"));
        assertAmount("-3184.575", data.get("2021"));
        assertAmount("-12.5", data.get("2022"));
    }

    @Test
    void countsByYear() {
        Map<String, BigDecimal> data = calculate(GroupOption.YEAR, AggregateOption.COUNT).data();

        assertAmount("4", data.get("2020"));
        assertAmount("2", data.get("2021"));
        assertAmount("1", data.get("2022"));
    }

    @Test
    void sumsByCategoryInAlphabeticalOrder() {
        Map<String, BigDecimal> data = calculate(GroupOption.CATEGORY, AggregateOption.SUM).data();

        assertEquals(List.of("Entertainment", "Food", "Salary", "Transport"), List.copyOf(data.keySet()));
        assertAmount("-10284.15", data.get("Entertainment"));
        assertAmount("-38.00", data.get("Food"));
        assertAmount("4090.00", data.get("Salary"));
        assertAmount("-15.00", data.get("Transport"));
    }

    @Test
    void averagesByCategory() {
        Map<String, BigDecimal> data = calculate(GroupOption.CATEGORY, AggregateOption.AVERAGE).data();

        assertAmount("2045", data.get("Salary"));
        assertAmount("-5142.075", data.get("Entertainment"));
        assertAmount("-19", data.get("Food"));
    }

    @Test
    void returnsEachTransactionWithoutGrouping() {
        Map<String, BigDecimal> data = calculate(GroupOption.NONE, AggregateOption.SUM).data();

        assertEquals(List.of("2", "4", "6", "8", "10", "12", "14"), List.copyOf(data.keySet()));
        assertAmount("-3825.00", data.get("6"));
        assertAmount("-6459.15", data.get("12"));
    }

    @Test
    void sumsByAccountType() {
        Map<String, BigDecimal> data = calculate(GroupOption.ACCOUNT_TYPE, AggregateOption.SUM).data();

        assertEquals(List.of("текущий", "сберегательный", "кредитный"), List.copyOf(data.keySet()));
        assertAmount("-3865.50", data.get("текущий"));
        assertAmount("4090.00", data.get("сберегательный"));
        assertAmount("-6471.65", data.get("кредитный"));
    }

    @Test
    void sumsByUser() {
        Map<String, BigDecimal> data = calculate(GroupOption.USER_ID, AggregateOption.SUM).data();

        assertAmount("224.50", data.get("пользователь 1"));
        assertAmount("-6471.65", data.get("пользователь 2"));
    }

    @Test
    void splitsIncomeAndExpenses() {
        Map<String, BigDecimal> data = calculate(GroupOption.INCOME_EXPENSE, AggregateOption.SUM).data();

        assertAmount("4090.00", data.get("доходы"));
        assertAmount("-10337.15", data.get("расходы"));
    }

    @Test
    void appliesFilterFromSpecificationExample() {
        TransactionFilter filter = TransactionFilter.empty()
                .withDateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 5, 11))
                .withAmountRange(new BigDecimal("-40"), new BigDecimal("1000"));

        Map<String, BigDecimal> data = service
                .calculate(filter, GroupOption.CATEGORY, AggregateOption.SUM)
                .data();

        assertEquals(List.of("Food", "Salary", "Transport"), List.copyOf(data.keySet()));
        assertAmount("-25.50", data.get("Food"));
        assertAmount("90.00", data.get("Salary"));
        assertAmount("-15.00", data.get("Transport"));
    }

    @Test
    void returnsEmptyDataWhenNothingMatches() {
        TransactionFilter filter = TransactionFilter.empty().withCategory("Travel");

        AnalyticsResult result = service.calculate(filter, GroupOption.CATEGORY, AggregateOption.SUM);

        assertTrue(result.data().isEmpty());
    }

    @Test
    void storesCalculationTimeTruncatedToSeconds() {
        AnalyticsResult result = calculate(GroupOption.YEAR, AggregateOption.SUM);

        assertEquals(LocalDateTime.of(2026, 10, 9, 12, 0, 0), result.calculatedAt());
    }

    private AnalyticsResult calculate(GroupOption groupOption, AggregateOption aggregateOption) {
        return service.calculate(TransactionFilter.empty(), groupOption, aggregateOption);
    }

    private static AccountRepository accounts(Account... accounts) {
        Map<Long, Account> byId = Arrays.stream(accounts)
                .collect(Collectors.toMap(Account::getId, Function.identity()));

        return new AccountRepository() {
            @Override
            public List<Account> findAll() {
                return List.copyOf(byId.values());
            }

            @Override
            public Optional<Account> findById(long accountId) {
                return Optional.ofNullable(byId.get(accountId));
            }
        };
    }
}