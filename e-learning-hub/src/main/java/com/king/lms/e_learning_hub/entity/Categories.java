package com.king.lms.e_learning_hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Categories extends BaseEntity {

    String name;
    @Column(unique = true)
    String slug;

}
