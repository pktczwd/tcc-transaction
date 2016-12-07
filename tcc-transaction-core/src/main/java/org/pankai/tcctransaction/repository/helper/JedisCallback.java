package org.pankai.tcctransaction.repository.helper;

import redis.clients.jedis.Jedis;

/**
 * Created by pktczwd on 2016/12/7.
 */
public interface JedisCallback<T> {

    public T doInJedis(Jedis jedis);
}
