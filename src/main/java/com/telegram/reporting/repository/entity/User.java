package com.telegram.reporting.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Telegram User entity.
 */
@Data
@Entity
@Table(name = "user", schema = "public")
@EqualsAndHashCode()
public class User {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "phone", nullable = false)
    private String phone;

    @OneToMany
    private List<Report> reports;

    public String getFullName() {
        return name + " " + surname;
    }
}
