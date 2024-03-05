package com.telegram.reporting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.telegram.reporting.domain.Role;
import com.telegram.reporting.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeTO implements Ordinal {

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
        return Objects.nonNull(activated);
    }

    @Override
    public Long getOrdinal() {
        return ordinalNumber;
    }
}
