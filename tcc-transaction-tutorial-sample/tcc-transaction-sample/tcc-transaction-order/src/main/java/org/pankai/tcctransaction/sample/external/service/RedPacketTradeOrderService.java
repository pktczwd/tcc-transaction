package org.pankai.tcctransaction.sample.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.pankai.tcctransaction.api.TransactionContext;
import org.pankai.tcctransaction.sample.external.dto.RedPacketTradeOrderDto;
import org.pankai.tcctransaction.sample.external.service.request.RedPacketTradeOrderRecordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Component
public class RedPacketTradeOrderService {

    private static final Logger logger = LoggerFactory.getLogger(RedPacketTradeOrderService.class);

    @Autowired
    private CloseableHttpClient closeableHttpClient;
    @Autowired
    private ObjectMapper objectMapper;

    public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        logger.info("RedPacketTradeOrderService record method called.");
        logger.info("Transaction ID:" + transactionContext.getXid().toString());
        logger.info("Transaction status:" + transactionContext.getStatus());

        try {
            RedPacketTradeOrderRecordRequest request = new RedPacketTradeOrderRecordRequest();
            request.setTransactionContext(transactionContext);
            request.setRedPacketTradeOrderDto(tradeOrderDto);
            HttpPost post = new HttpPost("http://127.0.0.1:7002/record");
            post.addHeader("Content-Type", "application/json");
            StringEntity stringEntity = new StringEntity(objectMapper.writeValueAsString(request));
            post.setEntity(stringEntity);
            CloseableHttpResponse response = closeableHttpClient.execute(post);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Red packet trade order record request failed.");
            }
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
