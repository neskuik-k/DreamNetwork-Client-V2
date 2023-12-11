package be.alexandre01.dreamnetwork.api.utils.cache;


import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;

import java.util.ArrayList;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 22/11/2023 at 22:42
*/
public class MemoryCache<K, V>{

    private final long timeToLive;
    private final LRUMap<K,CacheObject> cacheMap;

    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public V value;

        protected CacheObject(V value) {
            this.value = value;
        }
    }

    public MemoryCache(long timeToLive, final long timerInterval, int maxItems) {
        this.timeToLive = timeToLive * 1000;
        cacheMap = new LRUMap<>(maxItems);

        if (timeToLive > 0 && timerInterval > 0) {

            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(timerInterval * 1000);
                    } catch (InterruptedException ignored) {
                    }
                    cleanup();
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }
    public void cleanup() {
        // System: The System class contains several useful class fields and methods.
        // It cannot be instantiated. Among the facilities provided by the System class are standard input, standard output,
        // and error output streams; access to externally defined properties and environment variables;
        // a means of loading files and libraries; and a utility method for quickly copying a portion of an array.
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;
        synchronized (cacheMap) {
            MapIterator itr = cacheMap.mapIterator();
            // ArrayList: Constructs an empty list with the specified initial capacity.
            // size(): Gets the size of the map.
            deleteKey = new ArrayList<K>((cacheMap.size() / 2) + 1);
            K key = null;
            CacheObject c = null;
            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheObject) itr.getValue();
                if (c != null && (now > (timeToLive + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }
        for (K key : deleteKey) {
            synchronized (cacheMap) {
                // remove(): Removes the specified mapping from this map.
                cacheMap.remove(key);
            }
            // yield(): A hint to the scheduler that the current thread is willing to
            // yield its current use of a processor.
            // The scheduler is free to ignore this hint.
            Thread.yield();
        }
    }

    public void put(K key, V value) {
        synchronized (cacheMap) {

            // put(): Puts a key-value mapping into this map.
            cacheMap.put(key, new CacheObject(value));
        }
    }
    public V get(K key) {
        synchronized (cacheMap) {
            CacheObject c;
            c = (CacheObject) cacheMap.get(key);
            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }
    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    public boolean contains(K key) {
        synchronized (cacheMap) {
            return cacheMap.containsKey(key);
        }
    }
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

}
