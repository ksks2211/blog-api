package org.example.postapi.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author rival
 * @since 2025-01-15
 */


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private Role role;


    @ManyToOne
    @ToString.Exclude
    private AppUser appUser;


    public UserRole(AppUser appUser, Role role) {
        this.appUser = appUser;
        this.role = role;
    }
}
