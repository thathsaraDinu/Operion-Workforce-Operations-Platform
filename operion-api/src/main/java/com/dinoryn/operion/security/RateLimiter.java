package com.dinoryn.operion.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> {
            // Allow 3 requests per 15 minutes per key (email/IP)
            Bandwidth limit = Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(15)));
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    public boolean tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(1);
    }

    public void removeBucket(String key) {
        buckets.remove(key);
    }
}
