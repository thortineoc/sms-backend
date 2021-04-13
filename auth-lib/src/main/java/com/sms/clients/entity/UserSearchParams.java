package com.sms.clients.entity;

import javax.ws.rs.client.WebTarget;
import java.util.Optional;

public class UserSearchParams {

    private final Boolean briefRepresentation;
    private final String email;
    private final Integer first;
    private final String firstName;
    private final String lastName;
    private final Integer max;
    private final String search;
    private final String username;

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Boolean> getBriefRepresentation() {
        return Optional.ofNullable(briefRepresentation);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<Integer> getFirst() {
        return Optional.ofNullable(first);
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<Integer> getMax() {
        return Optional.ofNullable(max);
    }

    public Optional<String> getSearch() {
        return Optional.ofNullable(search);
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public WebTarget addParams(WebTarget target) {
        getUsername().ifPresent(p -> target.queryParam("username", p));
        getSearch().ifPresent(p -> target.queryParam("search", p));
        getFirst().ifPresent(p -> target.queryParam("first", p));
        getFirstName().ifPresent(p -> target.queryParam("firstName", p));
        getLastName().ifPresent(p -> target.queryParam("lastName", p));
        getMax().ifPresent(p -> target.queryParam("max", p));
        getEmail().ifPresent(p -> target.queryParam("email", p));
        getBriefRepresentation().ifPresent(p -> target.queryParam("briefRepresentation", p));
        return target;
    }

    private UserSearchParams(Builder builder) {
        this.briefRepresentation = builder.briefRepresentation;
        this.email = builder.email;
        this.first = builder.first;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.max = builder.max;
        this.search = builder.search;
        this.username = builder.username;
    }

    public static class Builder {
        private Boolean briefRepresentation;
        private String email;
        private Integer first;
        private String firstName;
        private String lastName;
        private Integer max;
        private String search;
        private String username;

        public Builder briefRepresentation(Boolean briefRepresentation) {
            this.briefRepresentation = briefRepresentation;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder first(Integer first) {
            this.first = first;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder max(Integer max) {
            this.max = max;
            return this;
        }

        public Builder search(String search) {
            this.search = search;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public UserSearchParams build() {
            return new UserSearchParams(this);
        }
    }
}
