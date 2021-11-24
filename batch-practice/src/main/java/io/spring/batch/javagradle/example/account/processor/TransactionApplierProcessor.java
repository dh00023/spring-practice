package io.spring.batch.javagradle.example.account.processor;

import io.spring.batch.javagradle.example.account.dao.TransactionDao;
import io.spring.batch.javagradle.example.account.domain.AccountSummary;
import io.spring.batch.javagradle.example.account.domain.Transaction;
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
