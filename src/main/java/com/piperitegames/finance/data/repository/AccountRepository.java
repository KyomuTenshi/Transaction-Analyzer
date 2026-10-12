package com.piperitegames.finance.data.repository;

import com.piperitegames.finance.data.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    List<Account> findAll();

    Optional<Account> findById(long accountId);
}