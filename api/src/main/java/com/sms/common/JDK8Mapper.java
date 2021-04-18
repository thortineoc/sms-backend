package com.sms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class JDK8Mapper extends ObjectMapper {

    public JDK8Mapper() {
        super();
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        registerModule(new Jdk8Module());
    }
}
