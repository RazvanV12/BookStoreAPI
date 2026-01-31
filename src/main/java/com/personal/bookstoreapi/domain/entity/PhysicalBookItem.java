package com.personal.bookstoreapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("PHYSICAL")
public class PhysicalBookItem extends BookItem {

    @Column(nullable = false)
    private Integer stockQuantity;

    private Integer weightGrams;
}