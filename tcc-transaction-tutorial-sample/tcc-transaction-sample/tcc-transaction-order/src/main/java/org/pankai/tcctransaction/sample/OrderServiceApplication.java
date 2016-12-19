package org.pankai.tcctransaction.sample;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Created by pktczwd on 2016/12/16.
 */
@EnableAutoConfiguration
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return HttpClients.custom().build();
    }

}
