package com.telegram.reporting.repository.filter;

import com.telegram.reporting.repository.entity.Role;

import java.util.Arrays;
import java.util.Set;

public record UserFilter(String name, String surname,
                         Set<Role> roles,
                         com.telegram.reporting.repository.filter.UserFilter.UserStatus[] userStatus) {

    public static UserFilter.UserFilterBuilder builder() {
        return new UserFilter.UserFilterBuilder();
    }

    public enum UserStatus {
        ACTIVE,
        ACTIVE_NOT_VERIFIED,
        DELETED
    }

    public static class UserFilterBuilder {

        private String name;
        private String surname;
        private Set<Role> roles;
        private UserFilter.UserStatus[] userStatus;

        UserFilterBuilder() {
        }

        public UserFilter.UserFilterBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public UserFilter.UserFilterBuilder surname(final String surname) {
            this.surname = surname;
            return this;
        }

        public UserFilter.UserFilterBuilder roles(final Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public UserFilter.UserFilterBuilder userStatus(final UserFilter.UserStatus... userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public UserFilter build() {
            return new UserFilter(this.name, this.surname, this.roles, this.userStatus);
        }

        public String toString() {

            return "UserFilter.UserFilterBuilder(name=" + this.name + ", surname=" + this.surname + ", roles=" + this.roles + ", userStatus=" + Arrays.deepToString(this.userStatus) + ")";
        }
    }
}
