package be.alexandre01.dreamnetwork.core.connection.core.communication;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 22/11/2023 at 22:39
*/


import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.utils.cache.MemoryCache;
import be.alexandre01.dreamnetwork.api.utils.optional.Facultative;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RateLimiter {
    private final long maxToken;
    private final MemoryCache<String, RateLimiterData> cache;

    public RateLimiter(){
        this(1000L, 10L);
    }

    public RateLimiter(Long timeToLive, Long maxToken){
        this.maxToken = maxToken;
        cache = new MemoryCache<>(timeToLive, 1000*60*20,9999);
    }

    public boolean isRateLimited(String ip){
        if(cache.contains(ip)){
            if(cache.get(ip).token > 0){
                cache.get(ip).consume();
                return false;
            }else{
                return true;
            }
        }
        cache.put(ip, new RateLimiterData(ip, maxToken));
        return false;
    }

    public class RateLimiterData{
        private final String ip;
        private long currentToken;
        private final long token;

        public RateLimiterData(String ip, long token) {
            this.ip = ip;
            this.token = token;
            fill();
        }

        public void fill(){
            currentToken = token;
        }

        public void removeTotally(){
            cache.remove(ip);
        }

        public void consume(){
            currentToken--;
        }
    }
}
