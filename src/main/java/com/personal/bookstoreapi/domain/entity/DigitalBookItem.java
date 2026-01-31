package com.personal.bookstoreapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("DIGITAL")
public class DigitalBookItem extends BookItem {

    @Column(length = 500)
    private String fileUrl;

    @Column(length = 20)
    private String fileFormat; // PDF, EPUB, MOBI, etc.
}