package com.piperitegames.finance.data.repository.file;

import com.piperitegames.finance.data.model.Account;
import com.piperitegames.finance.data.parser.AccountParser;
import com.piperitegames.finance.data.repository.AccountRepository;
import com.piperitegames.finance.exception.DuplicateRecordException;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileAccountRepository implements AccountRepository {

    private final Map<Long, Account> accountsById;

    public FileAccountRepository(Path path) {
        Map<Long, Account> accounts = new LinkedHashMap<>();
        for (Account account : TextFileReader.readAll(path, new AccountParser())) {
            if (accounts.putIfAbsent(account.getId(), account) != null) {
                throw new DuplicateRecordException(path, "ID счёта", account.getId());
            }
        }
        this.accountsById = Collections.unmodifiableMap(accounts);
    }

    @Override
    public List<Account> findAll() {
        return List.copyOf(accountsById.values());
    }

    @Override
    public Optional<Account> findById(long accountId) {
        return Optional.ofNullable(accountsById.get(accountId));
    }
}