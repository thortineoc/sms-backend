package com.sms.usermanagementservice.entity;

import java.util.Set;

public class User {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String username;
    private final Set<String> roles;
    private final String group;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getGroup() {
        return group;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    private User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.username = builder.username;
        this.roles = builder.roles;
        this.group = builder.group;
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private Set<String> roles;
        private String group;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
