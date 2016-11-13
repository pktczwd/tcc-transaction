package org.pankai.tcctransaction.api;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by pankai on 2016/11/13.
 */
public class TransactionXid implements Xid, Serializable {

    private int formatId = 1;
    private byte[] globalTransactionId;
    private byte[] branchQualifier;

    public TransactionXid() {
        globalTransactionId = uuidToByteArray(UUID.randomUUID());
        branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public TransactionXid(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public TransactionXid(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }


    @Override
    public int getFormatId() {
        return formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    @Override
    public String toString() {
        return UUID.nameUUIDFromBytes(globalTransactionId).toString() + "|" + UUID.nameUUIDFromBytes(branchQualifier).toString();
    }


    public TransactionXid clone() {
        byte[] cloneGlobalTransactionId = new byte[globalTransactionId.length];
        byte[] cloneBranchQualifier = new byte[branchQualifier.length];

        System.arraycopy(getGlobalTransactionId(), 0, cloneGlobalTransactionId, 0, globalTransactionId.length);
        System.arraycopy(getBranchQualifier(), 0, cloneBranchQualifier, 0, branchQualifier.length);

        TransactionXid clone = new TransactionXid(cloneGlobalTransactionId, cloneBranchQualifier);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionXid that = (TransactionXid) o;

        if (formatId != that.formatId) return false;
        if (!Arrays.equals(globalTransactionId, that.globalTransactionId)) return false;
        return Arrays.equals(branchQualifier, that.branchQualifier);

    }

    @Override
    public int hashCode() {
        int result = formatId;
        result = 31 * result + Arrays.hashCode(globalTransactionId);
        result = 31 * result + Arrays.hashCode(branchQualifier);
        return result;
    }

    public static byte[] uuidToByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID byteArrayToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

}
