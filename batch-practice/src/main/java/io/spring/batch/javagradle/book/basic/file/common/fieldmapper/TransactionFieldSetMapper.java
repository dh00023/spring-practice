package io.spring.batch.javagradle.book.basic.file.common.fieldmapper;

import io.spring.batch.javagradle.book.basic.file.common.domain.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {
    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(fieldSet.readDate("transactionDate", "yyyy-MM-dd HH:mm:ss"));
        transaction.setAmount(fieldSet.readDouble("amount"));
        transaction.setAccountNumber(fieldSet.readString("accountNumber"));
        return transaction;
    }
}
