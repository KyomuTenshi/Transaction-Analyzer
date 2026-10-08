package com.piperitegames.finance.data.repository.file;

import com.piperitegames.finance.data.model.Transaction;
import com.piperitegames.finance.data.parser.TransactionParser;
import com.piperitegames.finance.data.repository.TransactionRepository;
import com.piperitegames.finance.exception.DuplicateRecordException;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public FileTransactionRepository(Path path) {
        List<Transaction> loaded = TextFileReader.readAll(path, new TransactionParser());
        Set<Long> ids = new HashSet<>();
        for (Transaction transaction : loaded) {
            if (!ids.add(transaction.getId())) {
                throw new DuplicateRecordException(path, "ID транзакции", transaction.getId());
            }
        }
        this.transactions = List.copyOf(loaded);
    }

    @Override
    public List<Transaction> findAll() {
        return transactions;
    }
}