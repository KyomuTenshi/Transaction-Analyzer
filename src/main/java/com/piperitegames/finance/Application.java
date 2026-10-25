package com.piperitegames.finance;

import com.piperitegames.finance.controller.AnalyticsSession;
import com.piperitegames.finance.controller.ConsoleIO;
import com.piperitegames.finance.controller.MainMenuController;
import com.piperitegames.finance.data.repository.AccountRepository;
import com.piperitegames.finance.data.repository.AnalyticRepository;
import com.piperitegames.finance.data.repository.TransactionRepository;
import com.piperitegames.finance.data.repository.file.FileAccountRepository;
import com.piperitegames.finance.data.repository.file.FileTransactionRepository;
import com.piperitegames.finance.data.repository.file.JsonAnalyticRepository;
import com.piperitegames.finance.exception.FinanceAnalyzerException;
import com.piperitegames.finance.exception.InputClosedException;
import com.piperitegames.finance.service.DefaultReportService;
import com.piperitegames.finance.service.DefaultTransactionService;
import com.piperitegames.finance.service.ReportService;
import com.piperitegames.finance.service.TransactionService;

import java.nio.file.Path;

public class Application {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Необходимо указать имена файлов для входных данных аккаунтов и транзакций, "
                    + "а также для выходного файла.");
            System.err.println("Пример: accounts.txt transactions.txt analytics.json");
            System.exit(1);
        }

        Path accountPath = Path.of(args[0]);
        Path transactionPath = Path.of(args[1]);
        Path outputPath = Path.of(args[2]);

        try {
            AccountRepository accountRepository = new FileAccountRepository(accountPath);
            TransactionRepository transactionRepository = new FileTransactionRepository(transactionPath);
            AnalyticRepository analyticRepository = new JsonAnalyticRepository(outputPath);

            TransactionService transactionService =
                    new DefaultTransactionService(transactionRepository, accountRepository);
            ReportService reportService = new DefaultReportService(analyticRepository);

            ConsoleIO console = new ConsoleIO();
            System.out.printf("Загружено счетов: %d, транзакций: %d%n",
                    accountRepository.findAll().size(), transactionRepository.findAll().size());

            new MainMenuController(console, new AnalyticsSession(), transactionService, reportService).run();
        } catch (FinanceAnalyzerException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(1);
        } catch (InputClosedException e) {
            System.out.println();
            System.out.println("Ввод завершён. Выход из приложения.");
        }
    }
}