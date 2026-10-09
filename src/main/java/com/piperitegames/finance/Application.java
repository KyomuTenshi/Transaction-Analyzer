package com.piperitegames.finance;

import com.piperitegames.finance.data.repository.AccountRepository;
import com.piperitegames.finance.data.repository.TransactionRepository;
import com.piperitegames.finance.data.repository.file.FileAccountRepository;
import com.piperitegames.finance.data.repository.file.FileTransactionRepository;
import com.piperitegames.finance.data.repository.file.JsonAnalyticRepository;
import com.piperitegames.finance.exception.FinanceAnalyzerException;
import com.piperitegames.finance.service.DefaultReportService;
import com.piperitegames.finance.service.DefaultTransactionService;
import com.piperitegames.finance.service.ReportService;
import com.piperitegames.finance.service.TransactionService;
import com.piperitegames.finance.service.analytics.AggregateOption;
import com.piperitegames.finance.service.analytics.AnalyticsResult;
import com.piperitegames.finance.service.analytics.GroupOption;
import com.piperitegames.finance.service.filter.TransactionFilter;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

public class Application {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Необходимо указать имена файлов: счета, транзакции и выходной файл.");
            System.err.println("Пример: accounts.txt transactions.txt analytics.json");
            System.exit(1);
        }

        try {
            AccountRepository accountRepository = new FileAccountRepository(Path.of(args[0]));
            TransactionRepository transactionRepository = new FileTransactionRepository(Path.of(args[1]));
            TransactionService transactionService =
                    new DefaultTransactionService(transactionRepository, accountRepository);
            ReportService reportService = new DefaultReportService(new JsonAnalyticRepository(Path.of(args[2])));

            for (GroupOption groupOption : GroupOption.values()) {
                print(transactionService.calculate(TransactionFilter.empty(), groupOption, AggregateOption.SUM));
            }
            print(transactionService.calculate(TransactionFilter.empty(), GroupOption.YEAR, AggregateOption.AVERAGE));
            print(transactionService.calculate(TransactionFilter.empty(), GroupOption.YEAR, AggregateOption.COUNT));

            TransactionFilter filter = TransactionFilter.empty()
                    .withDateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 5, 11))
                    .withAmountRange(new BigDecimal("-40"), new BigDecimal("1000"));
            AnalyticsResult filtered = transactionService.calculate(filter, GroupOption.CATEGORY, AggregateOption.SUM);
            print(filtered);

            reportService.save(filtered);
            System.out.println("Сохранено в " + args[2]);
        } catch (FinanceAnalyzerException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void print(AnalyticsResult result) {
        System.out.println("=== " + result.title() + " | " + result.filter().describe());
        result.data().forEach((key, value) -> System.out.println("  " + key + ": " + value.toPlainString()));
    }
}