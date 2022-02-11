package io.spring.batch.javagradle.book.example.account.dao;

import io.spring.batch.javagradle.book.example.account.domain.Transaction;

import java.util.List;

public interface TransactionDao {
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);
}