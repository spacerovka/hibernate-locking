package com.spacerovka.cahce;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class CachableChild {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public CachableChild() {

    }

    public CachableChild(String name) {
        this.name = name;
    }
}
