package com.piperitegames.finance.service.analytics;

import com.piperitegames.finance.data.model.Account;

import java.util.Optional;

@FunctionalInterface
public interface AccountLookup {

    Optional<Account> find(long accountId);
}