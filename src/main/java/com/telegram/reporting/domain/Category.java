package com.telegram.reporting.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "category", schema = "public")
@EqualsAndHashCode()
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name_key")
    private String nameKey;

    @Column(name = "description_key")
    private String descriptionKey;

    @Column(name = "deleted")
    private boolean deleted;
}
