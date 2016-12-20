package org.pankai.tcctransaction.sample.redpacket.domain.repository;

import org.pankai.tcctransaction.sample.redpacket.domain.entity.RedPacketAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Repository
public class RedPacketAccountRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public RedPacketAccount findByUserId(long userId) {
        return jdbcTemplate.queryForObject("select * from red_red_packet_account where user_id = ?", new RowMapper<RedPacketAccount>() {
            @Override
            public RedPacketAccount mapRow(ResultSet rs, int i) throws SQLException {
                RedPacketAccount redPacketAccount = new RedPacketAccount();
                redPacketAccount.setId(rs.getLong("red_packet_account_id"));
                redPacketAccount.setBalanceAmount(rs.getBigDecimal("balance_amount"));
                redPacketAccount.setUserId(rs.getLong("user_id"));
                return redPacketAccount;
            }
        }, userId);
    }

    public void save(RedPacketAccount redPacketAccount) {
        jdbcTemplate.update("update red_red_packet_account set balance_amount = ? where red_packet_account_id = ?", redPacketAccount.getBalanceAmount(), redPacketAccount.getId());
    }


}
