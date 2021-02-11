package io.thorntail.openshift.ts.rolling;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class Counters {
    private final ConcurrentMap<String, Integer> data = new ConcurrentHashMap<>();

    public void increment(String id) {
        data.merge(id, 1, Integer::sum);
    }

    public boolean containsKey(String id) {
        return data.containsKey(id);
    }

    public boolean containsValue(int value) {
        return data.containsValue(value);
    }

    public int size() {
        return data.size();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
