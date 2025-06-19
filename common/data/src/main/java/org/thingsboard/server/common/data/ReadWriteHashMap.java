package org.thingsboard.server.common.data;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.common.data
 * @date 2022/4/8 10:27
 * @Description:
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 通过读写锁封装的一个hashmap集合
 */
public class ReadWriteHashMap {
    //集合
    private final Map<String, String> m = new HashMap<>();

    //读写锁
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    //获取读锁
    private final Lock r = rwl.readLock();
    //获取写锁
    private final Lock w = rwl.writeLock();

    //读操作
    public String get(String key) {
        r.lock();
        try {
            if (m == null || m.size() == 0) {
                return "";
            }
            return m.get(key);
        } finally {
            r.unlock();
        }
    }

    //写操作
    public Object put(String key, String value) {
        w.lock();
        try {
            return m.put(key, value);
        } finally {
            w.unlock();
        }
    }

}
