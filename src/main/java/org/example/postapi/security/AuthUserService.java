package org.example.postapi.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.postapi.user.RegistrationProvider;
import org.example.postapi.user.entity.AppUser;
import org.example.postapi.user.AppUserService;
import org.example.postapi.user.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author rival
 * @since 2025-01-15
 */

@Service
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {

    private final AppUserService appUserService;


    @Override
    @Transactional
    public AuthUser loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserService.getAppUserByEmail(username);
        return appUserToAuthUser(appUser);
    }


    @Transactional
    public AuthUser loadUserById(UUID id){
        AppUser appUser = appUserService.getAppUserById(id);
        return appUserToAuthUser(appUser);
    }


    @Transactional
    public AuthUser createOrUpdateOAuth2Account(@NonNull RegistrationProvider provider,@NonNull String sub, String name, String email){
        AppUser appUser = appUserService.createOrUpdateAppUser(provider, sub, name, email);
        return appUserToAuthUser(appUser);
    }



    private Collection<GrantedAuthority> rolesToGrantedAuthorities(List<UserRole> roles){
        return roles.stream()
            .flatMap(userRole -> Stream.concat(
                Stream.of(new SimpleGrantedAuthority(userRole.getRole().getPrefixedRole())),
                userRole.getRole().getPrivileges().stream().map(privilege -> new SimpleGrantedAuthority(privilege.getPrivilege()))
            )).collect(Collectors.toSet());
    }

    private AuthUser appUserToAuthUser(AppUser appUser){

        // Extract authorities
        Collection<GrantedAuthority> authorities = rolesToGrantedAuthorities(appUser.getRoles());
        if(appUser.isSocial()){
            return new AuthUser(appUser.getId(), appUser.getRegistrationProvider(), appUser.getProvidedId(), authorities);
        }else{
            return new AuthUser(appUser.getId(), appUser.getEmail(), appUser.getPassword(), authorities);
        }
    }
}
