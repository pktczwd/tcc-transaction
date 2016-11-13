package org.pankai.tcctransaction.serializer;

import java.io.*;

/**
 * Created by pankai on 2016/11/13.
 */
public class JdkSerializationSerializer<T> implements ObjectSerializer<T> {

    @Override
    public byte[] serialize(T o) {
        if (o == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            try {
                ObjectOutputStream ex = new ObjectOutputStream(baos);
                ex.writeObject(o);
                ex.flush();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to serialize object of type:" + o.getClass(), e);
            }
            return baos.toByteArray();
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ex = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) ex.readObject();
            } catch (IOException e1) {
                throw new IllegalArgumentException("Failed to deserialize object", e1);
            } catch (ClassNotFoundException e2) {
                throw new IllegalStateException("Failed to deserialize object type", e2);
            }
        }
    }
}
