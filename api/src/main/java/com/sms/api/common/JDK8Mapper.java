package com.sms.api.common;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import static com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS;

public class JDK8Mapper extends ObjectMapper {

    private final JacksonJaxbJsonProvider JDK8Provider;

    public JDK8Mapper() {
        super();
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        registerModule(new Jdk8Module());
        registerModule(new JavaTimeModule());
        this.JDK8Provider = new JacksonJaxbJsonProvider(this, DEFAULT_ANNOTATIONS);
    }

    public JacksonJaxbJsonProvider getProvider() {
        return JDK8Provider;
    }
}
