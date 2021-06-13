package com.sms.clients.entity;

import java.util.Optional;

public class KcResult<T> {

    private final T content;
    private final boolean isOk;
    private final Integer errorCode;

    private KcResult(T content) {
        this.content = content;
        this.isOk = true;
        this.errorCode = null;
    }

    private KcResult(Integer errorCode) {
        this.content = null;
        this.isOk = false;
        this.errorCode = errorCode;
    }

    public static <T> KcResult<T> ok(T content) {
        return new KcResult<>(content);
    }

    public static <T> KcResult<T> fail(Integer errorCode) {
        return new KcResult<>(errorCode);
    }

    public boolean isOk() {
        return isOk;
    }

    public Optional<T> getContent() {
        return Optional.ofNullable(content);
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
