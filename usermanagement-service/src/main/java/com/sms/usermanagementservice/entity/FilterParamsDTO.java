package com.sms.usermanagementservice.entity;

import java.util.Optional;

public class FilterParamsDTO{

    private final Optional<String> group;
    private final Optional<String> phoneNumber;
    private final Optional<String> middleName;
    private final Optional<String> pesel;
    private final Optional<String> firstName;
    private final Optional<String> lastName;
    private final Optional<String> email;
    private final Optional<String> username;
    private final Optional<String> search;

    public Optional<String> getFirstName() {
        return firstName;
    }

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

    public Optional<String> getGroup() {
        return group;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<String> getMiddleName() {
        return middleName;
    }

    public Optional<String> getPesel() {
        return pesel;
    }

    public static FilterParamsDTO.Builder builder() {
        return new FilterParamsDTO.Builder();
    }

    private FilterParamsDTO(FilterParamsDTO.Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.username = builder.username;
        this.search = builder.search;
        this.group = builder.group;
        this.phoneNumber = builder.phoneNumber;
        this.middleName = builder.middleName;
        this.pesel = builder.pesel;
    }

    public static final class Builder {
        private Optional<String> firstName;
        private Optional<String> lastName;
        private Optional<String> email;
        private Optional<String> username;
        private Optional<String> search;
        private Optional<String> group;
        private Optional<String> phoneNumber;
        private Optional<String> middleName;
        private Optional<String> pesel;

        public FilterParamsDTO.Builder firstName(Optional<String> firstName) {
            this.firstName = firstName;
            return this;
        }

        public FilterParamsDTO.Builder lastName(Optional<String> lastName) {
            this.lastName = lastName;
            return this;
        }

        public FilterParamsDTO.Builder email(Optional<String> email) {
            this.email = email;
            return this;
        }

        public FilterParamsDTO.Builder username(Optional<String> username) {
            this.username = username;
            return this;
        }

        public FilterParamsDTO.Builder search(Optional<String> search) {
            this.search = search;
            return this;
        }
        public FilterParamsDTO.Builder group(Optional<String> group) {
            this.group = group;
            return this;
        }

        public FilterParamsDTO.Builder phoneNumber(Optional<String> phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public FilterParamsDTO.Builder middleName(Optional<String> middleName) {
            this.middleName = middleName;
            return this;
        }

        public FilterParamsDTO.Builder pesel(Optional<String> pesel) {
            this.pesel = pesel;
            return this;
        }
        public FilterParamsDTO build() {
            return new FilterParamsDTO(this);
        }
    }



}
