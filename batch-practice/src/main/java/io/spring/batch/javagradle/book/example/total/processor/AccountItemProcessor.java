package io.spring.batch.javagradle.book.example.total.processor;

import io.spring.batch.javagradle.book.example.total.domain.Nstatement;
import io.spring.batch.javagradle.book.example.total.extractor.AccountResultSetExtractor;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountItemProcessor implements ItemProcessor<Nstatement, Nstatement> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Nstatement process(Nstatement item) throws Exception {
        item.setAccounts(this.jdbcTemplate.query("select a.account_id, a.balance, a.last_statement_date" +
                ", t.transaction_id, t.description, t.credit, t.debit, t.timestamp " +
                "from naccount a left join ntransaction t on a.account_id = t.account_account_id " +
                "where a.account_id in (select account_account_id from ncustomer_account where customer_customer_id = ?) " +
                "order by t.timestamp", new AccountResultSetExtractor(), new Object[]{item.getCustomer().getCustomerId()}));
        return item;
    }
}
