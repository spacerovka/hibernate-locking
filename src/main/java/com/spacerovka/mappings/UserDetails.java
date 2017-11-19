package com.spacerovka.mappings;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_details")
@Data
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Name name;

    private Integer age;

    private String email;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

}
