package org.pankai.tcctransaction.utils;

import java.util.Collection;

/**
 * Created by pktczwd on 2016/12/7.
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }
}
