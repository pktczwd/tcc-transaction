package org.pankai.tcctransaction.recover;

/**
 * Created by pktczwd on 2016/12/7.
 */
public interface RecoverConfig {

    public int getMaxRetryCount();

    public int getRecoverDuration();

    public String getCronExpression();

}
