package com.piperitegames.finance;

public class Application {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Необходимо указать имена файлов: счета, транзакции и выходной файл.");
            System.err.println("Пример: accounts.txt transactions.txt analytics.json");
            System.exit(1);
        }

        String accountFilename = args[0];
        String transactionFilename = args[1];
        String outputFilename = args[2];

        System.out.println("Счета: " + accountFilename);
        System.out.println("Транзакции: " + transactionFilename);
        System.out.println("Результат: " + outputFilename);
    }
}