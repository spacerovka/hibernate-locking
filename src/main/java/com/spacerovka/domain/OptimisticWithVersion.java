package com.spacerovka.domain;

import lombok.Data;

import javax.annotation.Generated;
import javax.persistence.*;

/**
 * Created by Svetotulichka on 15.11.2017.
 */
@Entity
@Table(name = "optimistic_with_version")
@Data
public class OptimisticWithVersion {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @Version
    @GeneratedValue
    private int version;

    public OptimisticWithVersion(String name) {
        this.name = name;
    }

    public OptimisticWithVersion() {
    }
}
