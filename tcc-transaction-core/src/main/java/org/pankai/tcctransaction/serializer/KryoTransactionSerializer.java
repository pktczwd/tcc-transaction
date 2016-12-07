package org.pankai.tcctransaction.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.pankai.tcctransaction.InvocationContext;
import org.pankai.tcctransaction.Participant;
import org.pankai.tcctransaction.Terminator;
import org.pankai.tcctransaction.Transaction;
import org.pankai.tcctransaction.api.TransactionStatus;
import org.pankai.tcctransaction.api.TransactionXid;
import org.pankai.tcctransaction.common.TransactionType;

/**
 * Created by pankai on 2016/11/13.
 */
public class KryoTransactionSerializer implements ObjectSerializer<Transaction> {

    private static Kryo kryo = null;

    static {
        kryo = new Kryo();

        kryo.register(Transaction.class);
        kryo.register(TransactionXid.class);
        kryo.register(TransactionStatus.class);
        kryo.register(TransactionType.class);
        kryo.register(Participant.class);
        kryo.register(Terminator.class);
        kryo.register(InvocationContext.class);
    }

    @Override
    public byte[] serialize(Transaction transaction) {
        Output output = new Output(256, -1);
        kryo.writeObject(output, transaction);
        return output.toBytes();
    }

    @Override
    public Transaction deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        Transaction transaction = kryo.readObject(input, Transaction.class);
        return transaction;
    }
}
