package org.example.postapi.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.postapi.common.entity.BaseEntity;
import org.example.postapi.user.RegistrationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.example.postapi.user.AppUserConstants.COL_PROVIDED_ID;
import static org.example.postapi.user.AppUserConstants.COL_PROVIDER;

/**
 * @author rival
 * @since 2025-01-15
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {COL_PROVIDER, COL_PROVIDED_ID})})
public class AppUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;


    @JsonIgnore
    private String password;



    private String nickname;

    // OAuth2.0
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name= COL_PROVIDER)
    private RegistrationProvider registrationProvider = RegistrationProvider.LOCAL;
    @Builder.Default
    private boolean isSocial = false;

    @Column(name= COL_PROVIDED_ID)
    private String providedId;
    private String providedEmail;
    private String providedName;



    @OneToMany(mappedBy = "appUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<UserRole> roles = new ArrayList<>();


    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role);
        this.roles.add(userRole);
    }


    public void removeRole(Role role) {
        this.roles.removeIf(userRole -> userRole.getRole().equals(role));
    }

}
