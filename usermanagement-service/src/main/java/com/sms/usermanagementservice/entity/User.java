package com.sms.usermanagementservice.entity;

import com.sms.usermanagement.UserDTO;

import java.util.Map;
import java.util.Optional;

public class User {

    private final String firstName;
    private final String lastName;
    private final String username;
    private final UserDTO.Role role;
    private final Map<String, String> userAttributes;
    private final Optional<String> email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getUserAttributes() {
        return userAttributes;
    }

    public UserDTO.Role getRole() {
        return role;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public static Builder builder() {
        return new Builder();
    }

    private User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.username = builder.username;
        this.role = builder.role;
        this.userAttributes = builder.userAttributes;
        this.email = builder.email;
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String username;
        private UserDTO.Role role;
        private Map<String, String> userAttributes;
        private Optional<String> email = Optional.empty();

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder role(UserDTO.Role role) {
            this.role = role;
            return this;
        }

        public Builder userAttributes(Map<String, String> userAttributes) {
            this.userAttributes = userAttributes;
            return this;
        }

        public Builder email(Optional<String> email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
