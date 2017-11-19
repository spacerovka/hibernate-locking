package com.spacerovka.mappings;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DynamicInsert//to mot insert null values and use defaults
@Table(name = "all_columns")
@Data
public class AllColumnsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "yes_no")
    private boolean shared;

    @Type(type = "true_false")
    @Column(columnDefinition = "char(1) DEFAULT 'T' NOT NUll")
    private Boolean active;

    @Type(type = "numeric_boolean")
    private boolean bit;

    @Lob
    private String longDescription;

    @Column(scale = 2)
    private BigDecimal money;

    @Enumerated
    private Topic topic;

    public enum Topic {
        CORE, HIBERNATE, JPA, MYSQL
    }
}
