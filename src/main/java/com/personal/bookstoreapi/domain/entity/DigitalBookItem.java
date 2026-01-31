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
@DiscriminatorValue("DIGITAL")
public class DigitalBookItem extends BookItem {

    @Column(length = 500, name = "file_url")
    private String fileUrl;

    @Column(length = 20 , name = "file_format")
    private String fileFormat; // PDF, EPUB, MOBI, etc.
}