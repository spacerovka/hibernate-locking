package com.spacerovka.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@DynamicUpdate
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@Data
public class DirtyOptimistic {

    @Id
    @GeneratedValue
    private long id;

    private String value;

    private String otherValue;

    public DirtyOptimistic() {
    }

    public DirtyOptimistic(String value, String otherValue) {
        this.value = value;
        this.otherValue = otherValue;
    }
}
