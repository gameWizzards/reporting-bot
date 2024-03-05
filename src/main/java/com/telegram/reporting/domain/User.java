package com.telegram.reporting.domain;

import com.telegram.reporting.utils.LocaleConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "user", schema = "public")
@EqualsAndHashCode(exclude = {"reports", "deleted", "salaryReports"})
@ToString(exclude = {"reports"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "telegram_nickname")
    private String telegramNickname;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(name = "activated")
    private LocalDateTime activated;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Report> reports;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<SalaryReport> salaryReports;

    @Column(name = "locale")
    @Convert(converter = LocaleConverter.class)
    private Locale locale;

    public String getFullName() {
        return name + " " + surname;
    }

    public boolean isActivated() {
        return Objects.nonNull(activated);
    }
}
