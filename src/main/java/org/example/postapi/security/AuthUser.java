package org.example.postapi.security;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.postapi.user.RegistrationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-15
 */
@Setter
@Getter
@ToString
public class AuthUser extends User {

    private final UUID id;

    // Local User - email
    private String email;


    // Local User
    public AuthUser(UUID id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.id = id;
        this.email = email;
    }

    // OAuth2 User - subject e.g. google:<sub>
    private String subject;

    public AuthUser(UUID id, RegistrationProvider provider, String sub,Collection<? extends GrantedAuthority> authorities){
        super(provider.name()+":"+sub,"EMPTY-VALUE", authorities);
        this.id = id;
        this.subject = provider.name()+":"+sub;
    }

    public boolean isSocial(){
        return email == null;
    }

}
