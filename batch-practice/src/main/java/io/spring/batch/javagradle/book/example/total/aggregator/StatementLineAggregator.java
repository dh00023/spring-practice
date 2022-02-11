package io.spring.batch.javagradle.book.example.total.aggregator;

import io.spring.batch.javagradle.book.example.total.domain.Naccount;
import io.spring.batch.javagradle.book.example.total.domain.Ncustomer;
import io.spring.batch.javagradle.book.example.total.domain.Nstatement;
import io.spring.batch.javagradle.book.example.total.domain.Ntransaction;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;

public class StatementLineAggregator implements LineAggregator<Nstatement> {

    private static final String ADDRESS_LINE_ONE = String.format("%121s\n", "Apress Banking");
    private static final String ADDRESS_LINE_TWO = String.format("%120s\n", "1060 West Addison St.");
    private static final String ADDRESS_LINE_THREE = String.format("%120s\n\n", "Chicago, IL 60613");
    private static final String STATEMENT_DATE_LINE = String.format("Your Account Summary %78s\n", "Statement Period") + "%tD to %tD\n\n";


    @Override
    public String aggregate(Nstatement statement) {
        StringBuilder sb = new StringBuilder();

        formatHeader(statement, sb);
        formatAccount(statement, sb);

        return sb.toString();
    }

    private void formatAccount(Nstatement statement, StringBuilder sb) {
        if (!CollectionUtils.isEmpty(statement.getAccounts())) {
            for (Naccount account : statement.getAccounts()) {
                sb.append(String.format(STATEMENT_DATE_LINE, account.getLastStatementDate(), new Date()));

                BigDecimal creditAmount = BigDecimal.ZERO;
                BigDecimal debitAmount = BigDecimal.ZERO;

                for (Ntransaction transaction : account.getTransactions()) {
                    if (transaction.getCredit() != null) {
                        creditAmount = creditAmount.add(transaction.getCredit());
                    }

                    if (transaction.getDebit() != null) {
                        debitAmount = debitAmount.add(transaction.getDebit());
                    }

                    sb.append(String.format(" %tD %-50s %9.2f\n", transaction.getTimestamp(), transaction.getDescription(), transaction.getTransactionAmount()));
                }

                sb.append(String.format("%80s %14.2f\n", "Total Debit:", debitAmount));
                sb.append(String.format("%81s %13.2f\n", "Total Credit:", creditAmount));
                sb.append(String.format("%76s %18.2f\n\n", "Balance:", account.getBalance()));
            }
        }
    }

    private void formatHeader(Nstatement statement, StringBuilder sb) {
        Ncustomer customer = statement.getCustomer();

        String customerName = String.format("\n%s %s", customer.getFirstName(), customer.getLastName());

        sb.append(customerName + ADDRESS_LINE_ONE.substring(customerName.length()));
        sb.append(customer.getAddress1() + ADDRESS_LINE_TWO.substring(customer.getAddress1().length()));

        String addressString = String.format("%s, %s %s", customer.getCity(), customer.getState(), customer.getPostalCode());

        sb.append(addressString + ADDRESS_LINE_THREE.substring(addressString.length()));
    }
}
