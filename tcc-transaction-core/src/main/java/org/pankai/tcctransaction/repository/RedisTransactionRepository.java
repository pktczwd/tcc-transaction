package org.pankai.tcctransaction.repository;

import org.pankai.tcctransaction.Transaction;
import org.pankai.tcctransaction.common.TransactionType;
import org.pankai.tcctransaction.repository.helper.JedisCallback;
import org.pankai.tcctransaction.repository.helper.RedisHelper;
import org.pankai.tcctransaction.repository.helper.TransactionSerializer;
import org.pankai.tcctransaction.serializer.JdkSerializationSerializer;
import org.pankai.tcctransaction.serializer.ObjectSerializer;
import org.pankai.tcctransaction.utils.ByteUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by pktczwd on 2016/12/7.
 * <p/>
 * As the storage of transaction need safely durable,make sure the redis server is set as AOF mode and always fsync.
 * set below directives in your redis.conf
 * appendonly yes
 * appendfsync always
 */
public class RedisTransactionRepository extends CachableTransactionRepository {

    private JedisPool jedisPool;

    private String keyPrefix = "TCC:";

    private ObjectSerializer serializer = new JdkSerializationSerializer();

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public ObjectSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected int doCreate(Transaction transaction) {

        try {
            final byte[] key = RedisHelper.getRedisKey(keyPrefix, transaction.getXid());
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {

                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.hsetnx(key, ByteUtils.longToBytes(transaction.getVersion()), TransactionSerializer.serialize(serializer, transaction));
                }
            });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(Transaction transaction) {

        try {
            final byte[] key = RedisHelper.getRedisKey(keyPrefix, transaction.getXid());
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    transaction.updateTime();
                    transaction.updateVersion();
                    return jedis.hsetnx(key, ByteUtils.longToBytes(transaction.getVersion()), TransactionSerializer.serialize(serializer, transaction));
                }
            });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doDelete(Transaction transaction) {
        try {
            final byte[] key = RedisHelper.getRedisKey(keyPrefix, transaction.getXid());
            Long result = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.del(key);
                }
            });
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {
        try {

            final byte[] key = RedisHelper.getRedisKey(keyPrefix, xid);
            byte[] content = RedisHelper.getKeyValue(jedisPool, key);

            if (content != null) {
                return TransactionSerializer.deserialize(serializer, content);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected List<Transaction> doFindAllUnmodifiedSince(Date date) {

        List<Transaction> allTransactions = doFindAll();
        List<Transaction> allUnmodifiedSince = new ArrayList<Transaction>();
        
        for (Transaction transaction : allTransactions) {
            if (transaction.getTransactionType().equals(TransactionType.ROOT)
                    && transaction.getLastUpdateTime().compareTo(date) < 0) {
                allUnmodifiedSince.add(transaction);
            }
        }

        return allUnmodifiedSince;
    }

    protected List<Transaction> doFindAll() {

        try {
            List<Transaction> transactions = new ArrayList<Transaction>();
            Set<byte[]> keys = RedisHelper.execute(jedisPool, new JedisCallback<Set<byte[]>>() {
                @Override
                public Set<byte[]> doInJedis(Jedis jedis) {
                    return jedis.keys((keyPrefix + "*").getBytes());
                }
            });

            for (final byte[] key : keys) {
                byte[] content = RedisHelper.getKeyValue(jedisPool, key);

                if (content != null) {
                    transactions.add(TransactionSerializer.deserialize(serializer, content));
                }
            }

            return transactions;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }
}
