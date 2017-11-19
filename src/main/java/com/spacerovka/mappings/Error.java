package com.spacerovka.mappings;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Error {

    @Id
    @GeneratedValue
    private Long id;

    private String userError;
}
