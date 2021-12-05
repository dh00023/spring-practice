package io.spring.batch.javagradle.book.example.total.extractor;

import io.spring.batch.javagradle.book.example.total.domain.Naccount;
import io.spring.batch.javagradle.book.example.total.domain.Ntransaction;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 쿼리의 결과가 부모 자식간의 관계인 경우 RowMapper를 사용할 수 없음.
 * RowMapper는 단일 행을 객체에 매핑
 * ResultSetExtractor는 ResultSet전체를 본다.
 */
public class AccountResultSetExtractor implements ResultSetExtractor<List<Naccount>> {

    private List<Naccount> accounts = new ArrayList<>();
    private Naccount curAccount;

    @Nullable
    @Override
    public List<Naccount> extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            if (curAccount == null) {
                curAccount = new Naccount(
                        rs.getLong("account_id")
                        , rs.getBigDecimal("balance")
                        , rs.getDate("last_statement_date")
                );
            } else if (rs.getLong("account_id") != curAccount.getId()) {
                accounts.add(curAccount);
                curAccount = new Naccount(
                        rs.getLong("account_id")
                        , rs.getBigDecimal("balance")
                        , rs.getDate("last_statement_date"));
            }

            if (StringUtils.hasText(rs.getString("description"))) {
                curAccount.addTransaction(
                        new Ntransaction(rs.getLong("transaction_id")
                        , rs.getLong("account_id")
                        , rs.getString("description")
                        , rs.getBigDecimal("credit")
                        , rs.getBigDecimal("debit")
                        , new Date(rs.getTimestamp("timestamp").getTime()))
                );
            }

        }
        if (curAccount != null) {
            accounts.add(curAccount);
        }
        return accounts;
    }
}
