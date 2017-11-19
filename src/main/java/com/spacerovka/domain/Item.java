package com.spacerovka.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Svetotulichka on 15.11.2017.
 */
@Entity(name = "item")
@Data
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }
}
