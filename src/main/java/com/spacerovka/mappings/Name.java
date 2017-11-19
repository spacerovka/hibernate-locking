package com.spacerovka.mappings;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Name {

    private String firstName;

    private String middleName;

    private String lastName;

}
