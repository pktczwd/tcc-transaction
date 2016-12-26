package org.pankai.tcctransaction.sample;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.pankai.tcctransaction.repository.RedisTransactionRepository;
import org.pankai.tcctransaction.spring.EnableTccTransaction;
import org.pankai.tcctransaction.spring.repository.SpringJdbcTransactionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

/**
 * Created by pktczwd on 2016/12/19.
 */
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@EnableTccTransaction
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan
public class CapitalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapitalServiceApplication.class, args);
    }

    @Bean
    @Profile("db")
    public DataSource tccDataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/TCC?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Bean
    @Profile("db")
    public SpringJdbcTransactionRepository springJdbcTransactionRepository() throws Exception {
        SpringJdbcTransactionRepository springJdbcTransactionRepository = new SpringJdbcTransactionRepository();
        springJdbcTransactionRepository.setDataSource(tccDataSource());
        springJdbcTransactionRepository.setDomain("capital");
        springJdbcTransactionRepository.setTbSuffix("_cap");
        return springJdbcTransactionRepository;
    }

    @Bean
    @Profile("redis")
    public JedisPool jedisPool() {
        JedisPool jedisPool = new JedisPool("192.168.10.13", 6379);
        return jedisPool;
    }

    @Bean
    @Profile("redis")
    public RedisTransactionRepository redisTransactionRepository() {
        RedisTransactionRepository redisTransactionRepository = new RedisTransactionRepository();
        redisTransactionRepository.setJedisPool(jedisPool());
        return redisTransactionRepository;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/TCC_CAP?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws Exception {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}
