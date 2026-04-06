package com.conversion.pmk.payment.cache;

import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import com.conversion.pmk.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

// Manages Redis caching for bill details and payment sessions
@Slf4j
@Service
@RequiredArgsConstructor
public class BillCacheService {

    private static final String BILL_KEY_PREFIX    = "patient:bill:";
    private static final String PAYMENT_KEY_PREFIX = "payment:session:";
    private static final Duration BILL_TTL    = Duration.ofMinutes(5);
    private static final Duration PAYMENT_TTL = Duration.ofMinutes(15);

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheBillDetail(String personRef, BillDetailResponse response) {
        String key = BILL_KEY_PREFIX + personRef;
        redisTemplate.opsForValue().set(key, response, BILL_TTL);
        log.debug("Cached bill detail for personRef={}", personRef);
    }

    public Optional<BillDetailResponse> getCachedBillDetail(String personRef) {
        String key = BILL_KEY_PREFIX + personRef;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof BillDetailResponse detail) {
            log.debug("Cache hit for bill detail personRef={}", personRef);
            return Optional.of(detail);
        }
        return Optional.empty();
    }

    public void cachePaymentSession(String sessionRef, PaymentResponse response) {
        String key = PAYMENT_KEY_PREFIX + sessionRef;
        redisTemplate.opsForValue().set(key, response, PAYMENT_TTL);
        log.debug("Cached payment session for sessionRef={}", sessionRef);
    }

    public Optional<PaymentResponse> getCachedPaymentSession(String sessionRef) {
        String key = PAYMENT_KEY_PREFIX + sessionRef;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof PaymentResponse payment) {
            log.debug("Cache hit for payment session sessionRef={}", sessionRef);
            return Optional.of(payment);
        }
        return Optional.empty();
    }

    public void evictPaymentSession(String sessionRef) {
        String key = PAYMENT_KEY_PREFIX + sessionRef;
        redisTemplate.delete(key);
        log.debug("Evicted payment session cache for sessionRef={}", sessionRef);
    }
}
