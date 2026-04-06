package com.conversion.pmk.mock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Injects random failures so test suites can exercise error paths
@Slf4j
@Component
public class FailureSimulator {

    @Value("${pmk.mock.failure-rate:0.05}")
    private double failureRate;

    // Returns true ~failureRate % of the time
    public boolean shouldFail() {
        return Math.random() < failureRate;
    }

    // Returns true ~half as often as shouldFail()
    public boolean shouldTimeout() {
        return Math.random() < (failureRate / 2);
    }

    // Blocks the calling thread to simulate a slow upstream response
    public void simulateTimeout() throws InterruptedException {
        if (shouldTimeout()) {
            log.debug("Simulating upstream timeout (5s)");
            Thread.sleep(5000);
        }
    }
}
