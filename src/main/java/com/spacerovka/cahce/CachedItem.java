package com.spacerovka.cahce;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CachedItem {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    //unidirectional oneToMany, additional table would be geneated
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //column in junction table to delete only tail element from it when it is removed
    @OrderColumn(name = "entity")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<CachableChild> items = new ArrayList<>();
}
