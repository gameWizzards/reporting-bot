package com.telegram.reporting.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.telegram.reporting.repository.entity.Role;
import com.telegram.reporting.repository.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeTO implements Serializable, Ordinal {

    private Long id;
    private Long chatId;
    private String fullName;
    private String phone;
    private boolean deleted;
    private Set<Role> roles;
    private String telegramNickname;
    private LocalDateTime activated;
    private LocalDateTime created;

    private Long ordinalNumber;

    public EmployeeTO() {
    }

    public EmployeeTO(User user) {
        this.id = user.getId();
        this.chatId = user.getChatId();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.deleted = user.isDeleted();
        this.roles = user.getRoles();
        this.telegramNickname = user.getTelegramNickname();
        this.activated = user.getActivated();
        this.created = user.getCreated();
    }

    public boolean isActivated() {
        return activated != null;
    }

    @Override
    public Long getOrdinal() {
        return ordinalNumber;
    }
}
