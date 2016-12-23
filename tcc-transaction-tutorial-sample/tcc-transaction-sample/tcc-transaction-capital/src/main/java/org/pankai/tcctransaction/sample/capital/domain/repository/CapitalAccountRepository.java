package org.pankai.tcctransaction.sample.capital.domain.repository;

import org.pankai.tcctransaction.sample.capital.domain.entity.CapitalAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Repository
public class CapitalAccountRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CapitalAccount findByUserId(long userId) {
        List<CapitalAccount> list = jdbcTemplate.query("select * from cap_capital_account where user_id = ?", new RowMapper<CapitalAccount>() {
            @Override
            public CapitalAccount mapRow(ResultSet rs, int i) throws SQLException {
                CapitalAccount capitalAccount = new CapitalAccount();
                capitalAccount.setId(rs.getLong("capital_account_id"));
                capitalAccount.setBalanceAmount(rs.getBigDecimal("balance_amount"));
                capitalAccount.setUserId(rs.getLong("user_id"));
                return capitalAccount;
            }
        }, userId);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void save(CapitalAccount capitalAccount) {
        jdbcTemplate.update("update cap_capital_account set balance_amount = ? where capital_account_id = ?", capitalAccount.getBalanceAmount(), capitalAccount.getId());
    }
}
