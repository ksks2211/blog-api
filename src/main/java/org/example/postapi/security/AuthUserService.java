package org.example.postapi.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.postapi.domain.refresh.RefreshToken;
import org.example.postapi.domain.refresh.RefreshTokenService;
import org.example.postapi.domain.user.AppUserService;
import org.example.postapi.domain.user.RegistrationProvider;
import org.example.postapi.domain.user.entity.AppUser;
import org.example.postapi.domain.user.entity.UserRole;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.postapi.security.SecurityConstants.AUTHENTICATION_CACHE_NAME;

/**
 * @author rival
 * @since 2025-01-15
 */

@Service
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {

    private final AppUserService appUserService;
    private final RefreshTokenService refreshTokenService;


    @Override
    @Transactional
    public AuthUser loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserService.getAppUserByEmail(username);
        return appUserToAuthUser(appUser, false);
    }


    @Transactional
    @Cacheable(value=AUTHENTICATION_CACHE_NAME,key="#id")
    public AuthUser loadUserById(UUID id){
        AppUser appUser = appUserService.getAppUserById(id);
        return appUserToAuthUser(appUser, true);
    }


    @Transactional
    public AuthUser loadUserByRefreshToken(UUID tokenId){
        RefreshToken refreshToken = refreshTokenService.getRefreshTokenById(tokenId);
        AppUser user = refreshToken.getUser();
        return appUserToAuthUser(user, true);
    }


    @Transactional
    public AuthUser createOrUpdateOAuth2Account(@NonNull RegistrationProvider provider,@NonNull String sub, String name, String email){
        AppUser appUser = appUserService.createOrUpdateAppUser(provider, sub, name, email);
        return appUserToAuthUser(appUser, true);
    }



    private Collection<GrantedAuthority> rolesToGrantedAuthorities(List<UserRole> roles){
        return roles.stream()
            .flatMap(userRole -> Stream.concat(
                Stream.of(new CustomGrantedAuthority(userRole.getRole().getPrefixedRole())),
                userRole.getRole().getPrivileges().stream().map(privilege -> new CustomGrantedAuthority(privilege.getPrivilege()))
            )).collect(Collectors.toSet());
    }

    private AuthUser appUserToAuthUser(AppUser appUser, boolean eraseCredentials){

        // Extract authorities
        Collection<GrantedAuthority> authorities = rolesToGrantedAuthorities(appUser.getRoles());

        AuthUser user;
        if(appUser.isSocial()){
            user= new AuthUser(appUser.getId(), appUser.getRegistrationProvider(),appUser.getProvidedId(),appUser.getNickname(), authorities);
        }else{
            user= new AuthUser(appUser.getId(), appUser.getEmail(), appUser.getPassword(),appUser.getNickname(), authorities);
        }

        if(eraseCredentials){
            user.eraseCredentials();
        }
        return user;
    }
}
