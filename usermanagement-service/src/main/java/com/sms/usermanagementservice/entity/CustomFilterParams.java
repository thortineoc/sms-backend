package com.sms.usermanagementservice.entity;

import java.util.Optional;


public class CustomFilterParams {

    private final Optional<String> group;
    private final Optional<String> phoneNumber;
    private final Optional<String> middleName;
    private final Optional<String> pesel;
    private final Optional<String> role;

    public static CustomFilterParams.Builder builder() {
        return new CustomFilterParams.Builder();
    }

    private CustomFilterParams(CustomFilterParams.Builder builder) {
        this.group = builder.group;
        this.phoneNumber = builder.phoneNumber;
        this.middleName = builder.middleName;
        this.pesel = builder.pesel;
        this.role = builder.role;
    }

    public static final class Builder {
        private Optional<String> group;
        private Optional<String> phoneNumber;
        private Optional<String> middleName;
        private Optional<String> pesel;
        private Optional<String> role;

        public CustomFilterParams.Builder group(Optional<String> group) {
            this.group = group;
            return this;
        }

        public CustomFilterParams.Builder phoneNumber(Optional<String> phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public CustomFilterParams.Builder middleName(Optional<String> middleName) {
            this.middleName = middleName;
            return this;
        }

        public CustomFilterParams.Builder pesel(Optional<String> pesel) {
            this.pesel = pesel;
            return this;
        }

        public CustomFilterParams.Builder role(Optional<String> role) {
            this.role = role;
            return this;
        }

        public CustomFilterParams build() {
            return new CustomFilterParams(this);
        }
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

    public Optional<String> getRole() {
        return role;
    }


}
