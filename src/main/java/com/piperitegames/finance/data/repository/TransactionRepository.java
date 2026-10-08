package com.piperitegames.finance.data.repository;

import com.piperitegames.finance.data.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    List<Transaction> findAll();
}