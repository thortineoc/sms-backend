package com.sms.usermanagementservice.entity;

import java.util.Optional;

public class KeyCloakFilterParams {

    private final Optional<String> firstName;
    private final Optional<String> lastName;
    private final Optional<String> email;
    private final Optional<String> username;
    private final Optional<String> search;

    public static KeyCloakFilterParams.Builder builder() {
        return new KeyCloakFilterParams.Builder();
    }

    private KeyCloakFilterParams(KeyCloakFilterParams.Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.username = builder.username;
        this.search = builder.search;
    }

    public static final class Builder {
        private Optional<String> firstName;
        private Optional<String> lastName;
        private Optional<String> email;
        private Optional<String> username;
        private Optional<String> search;

        public KeyCloakFilterParams.Builder firstName(Optional<String> firstName) {
            this.firstName = firstName;
            return this;
        }

        public KeyCloakFilterParams.Builder lastName(Optional<String> lastName) {
            this.lastName = lastName;
            return this;
        }

        public KeyCloakFilterParams.Builder email(Optional<String> email) {
            this.email = email;
            return this;
        }

        public KeyCloakFilterParams.Builder username(Optional<String> username) {
            this.username = username;
            return this;
        }

        public KeyCloakFilterParams.Builder search(Optional<String> search) {
            this.search = search;
            return this;
        }

        public KeyCloakFilterParams build() {
            return new KeyCloakFilterParams(this);
        }
    }

    public  Optional<String> getFirstName() { return firstName; }

    public Optional<String> getLastName() {
        return lastName;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getSearch() {
        return search;
    }

}
