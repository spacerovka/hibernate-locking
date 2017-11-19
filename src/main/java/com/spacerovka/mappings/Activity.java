package com.spacerovka.mappings;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Activity {

    @Id
    @GeneratedValue
    private Long id;

    private int rating;

    @ManyToOne
    private User user;
}
