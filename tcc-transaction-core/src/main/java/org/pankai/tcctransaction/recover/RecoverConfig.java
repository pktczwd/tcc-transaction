package org.pankai.tcctransaction.recover;

/**
 * Created by pktczwd on 2016/12/7.
 */
public interface RecoverConfig {

    //事务最多重试次数
    public int getMaxRetryCount();

    //事务恢复时间范围,单位秒.
    public int getRecoverDuration();

    //事务执行表达式.
    public String getCronExpression();

}
