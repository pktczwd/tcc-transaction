package org.pankai.tcctransaction.sample;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.pankai.tcctransaction.spring.EnableTccTransaction;
import org.pankai.tcctransaction.spring.repository.SpringJdbcTransactionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by pktczwd on 2016/12/16.
 */
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@EnableTccTransaction
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return HttpClients.custom().build();
    }

    @Bean
    public DataSource tccDataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/TCC?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Bean
    public SpringJdbcTransactionRepository springJdbcTransactionRepository() throws Exception {
        SpringJdbcTransactionRepository springJdbcTransactionRepository = new SpringJdbcTransactionRepository();
        springJdbcTransactionRepository.setDataSource(tccDataSource());
        springJdbcTransactionRepository.setDomain("order");
        springJdbcTransactionRepository.setTbSuffix("_ord");
        return springJdbcTransactionRepository;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/TCC_ORD?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("admin");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        return new JdbcTemplate(dataSource());
    }

}
