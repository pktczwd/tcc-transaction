package org.pankai.tcctransaction.sample.order.factory;

import org.apache.commons.lang3.tuple.Pair;
import org.pankai.tcctransaction.sample.order.domain.entity.Order;
import org.pankai.tcctransaction.sample.order.domain.entity.OrderLine;
import org.pankai.tcctransaction.sample.order.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by pktczwd on 2016/12/19.
 */
@Component
public class OrderFactory {

    @Autowired
    private ProductRepository productRepository;

    public Order buildOrder(long payerUserId, long payeeUserId, List<Pair<Long, Integer>> productQuantities) {

        Order order = new Order(payerUserId, payeeUserId);

        for (Pair<Long, Integer> pair : productQuantities) {
            long productId = pair.getLeft();
            order.addOrderLine(new OrderLine(productId, pair.getRight(), productRepository.findById(productId).getPrice()));
        }

        return order;
    }

}
