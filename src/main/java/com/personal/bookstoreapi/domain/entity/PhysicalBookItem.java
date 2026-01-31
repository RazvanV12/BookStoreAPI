package com.personal.bookstoreapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("PHYSICAL")
public class PhysicalBookItem extends BookItem {

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "weight_grams")
    private Integer weightGrams;
}