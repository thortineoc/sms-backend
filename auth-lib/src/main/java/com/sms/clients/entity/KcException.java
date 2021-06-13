package com.sms.clients.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class KcException extends ResponseStatusException {

    public KcException(KcResult<?> result, String reason) {
        super(HttpStatus.valueOf(result.getErrorCode()), reason);
    }
}
