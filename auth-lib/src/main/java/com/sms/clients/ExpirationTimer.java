package com.sms.clients;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class ExpirationTimer {

    private final Stopwatch timer = Stopwatch.createUnstarted();
    private Integer expirationTime;
    private Integer refreshExpirationTime;

    private ExpirationTimer() {
    }

    Integer getExpirationTime() {
        return expirationTime;
    }

    Integer getRefreshExpirationTime() {
        return refreshExpirationTime;
    }

    public static ExpirationTimer createStopped() {
        return new ExpirationTimer();
    }

    public void start(Integer expirationTime, Integer refreshExpirationTime) {
        this.expirationTime = expirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
        timer.start();
    }

    public void reset() {
        this.expirationTime = null;
        this.refreshExpirationTime = null;
        timer.reset();
    }

    public boolean isExpired() {
        return expirationTime != null
                && timer.elapsed(TimeUnit.SECONDS) > expirationTime;
    }

    public boolean isRefreshExpired() {
        return refreshExpirationTime != null
                && timer.elapsed(TimeUnit.SECONDS) > refreshExpirationTime;
    }
}
