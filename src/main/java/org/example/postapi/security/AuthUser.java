package org.example.postapi.security;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.postapi.domain.user.RegistrationProvider;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-15
 */
@Setter
@Getter
@ToString
public class AuthUser implements UserDetails, CredentialsContainer, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    // Local User - email
    private String email;
    private String password;

    // OAuth2 User - subject e.g. google:<sub>
    private String subject;


    private String username;
    private Set<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private String nickname;



    private AuthUser(UUID id,String nickname, Collection<? extends GrantedAuthority> authorities){
        this.id=id;
        this.authorities = new HashSet<>(authorities);
        this.accountNonExpired=true;
        this.accountNonLocked=true;
        this.enabled=true;
        this.credentialsNonExpired=true;
        this.nickname=nickname;
    }

    // Local User
    public AuthUser(UUID id, String email, String password, String nickname, Collection<? extends GrantedAuthority> authorities) {
        this(id, nickname, authorities);
        this.username = email;
        this.password = password;
        this.email = email;
    }



    public AuthUser(UUID id, RegistrationProvider provider, String sub, String nickname, Collection<? extends GrantedAuthority> authorities){
        this(id, nickname, authorities);
        this.username =  provider.name()+":"+sub;
        this.password = "EMPTY-VALUE";
        this.subject = provider.name()+":"+sub;
    }



    @Override
    public void eraseCredentials() {
        this.password=null;
    }
}
