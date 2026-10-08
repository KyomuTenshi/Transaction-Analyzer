package com.piperitegames.finance;

import com.piperitegames.finance.data.model.Transaction;
import com.piperitegames.finance.data.repository.AccountRepository;
import com.piperitegames.finance.data.repository.TransactionRepository;
import com.piperitegames.finance.data.repository.file.FileAccountRepository;
import com.piperitegames.finance.data.repository.file.FileTransactionRepository;
import com.piperitegames.finance.exception.FinanceAnalyzerException;

import java.nio.file.Path;

public class Application {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Необходимо указать имена файлов: счета, транзакции и выходной файл.");
            System.err.println("Пример: accounts.txt transactions.txt analytics.json");
            System.exit(1);
        }

        Path accountPath = Path.of(args[0]);
        Path transactionPath = Path.of(args[1]);

        try {
            AccountRepository accountRepository = new FileAccountRepository(accountPath);
            TransactionRepository transactionRepository = new FileTransactionRepository(transactionPath);

            System.out.println("Загружено счетов: " + accountRepository.findAll().size());
            System.out.println("Загружено транзакций: " + transactionRepository.findAll().size());

            for (Transaction transaction : transactionRepository.findAll()) {
                System.out.printf("%d [%s]: %s -> %s%n",
                        transaction.getId(),
                        transaction.getType().getCode(),
                        transaction.getAmount(),
                        transaction.getRealAmount());
            }
        } catch (FinanceAnalyzerException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(1);
        }
    }
}