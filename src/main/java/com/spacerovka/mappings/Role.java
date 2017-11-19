package com.spacerovka.mappings;

import lombok.Data;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    private RoleName name;

    @Enumerated
    @ElementCollection
    private Set<Authority> authorities;

    public Role() {
        authorities = EnumSet.of(Authority.VIEW);
    }

    public enum RoleName {
        USER, ADMIN, MANAGER
    }

    public enum Authority {
        POST, DELETE, VIEW
    }
}
