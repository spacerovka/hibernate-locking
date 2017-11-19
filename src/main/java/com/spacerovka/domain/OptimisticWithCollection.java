package com.spacerovka.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Svetotulichka on 15.11.2017.
 */
@Entity(name = "collection")
@Data
public class OptimisticWithCollection {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Version
    private int version;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    public OptimisticWithCollection() {
    }

    public OptimisticWithCollection(String name) {
        this.name = name;
    }

    public void addItem(Item item){
        items.add(item);
    }
}
