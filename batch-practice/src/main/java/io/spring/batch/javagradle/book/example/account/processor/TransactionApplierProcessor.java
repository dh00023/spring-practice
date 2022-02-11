package io.spring.batch.javagradle.book.example.account.processor;

import io.spring.batch.javagradle.book.example.account.domain.AccountSummary;
import io.spring.batch.javagradle.book.example.account.domain.Transaction;
import io.spring.batch.javagradle.book.example.account.dao.TransactionDao;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

@AllArgsConstructor
public class TransactionApplierProcessor implements ItemProcessor<AccountSummary, AccountSummary> {

    private TransactionDao transactionDao;


    @Override
    public AccountSummary process(AccountSummary item) throws Exception {
        List<Transaction> transactions = transactionDao.getTransactionsByAccountNumber(item.getAccountNumber());

        for (Transaction transaction : transactions) {
            item.setCurrentBalance(item.getCurrentBalance() + transaction.getAmount());
        }

        return item;
    }
}
