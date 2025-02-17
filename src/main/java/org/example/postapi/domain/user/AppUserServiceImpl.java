package org.example.postapi.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.domain.user.exception.AppUserNotFoundException;
import org.example.postapi.domain.user.exception.EmailAlreadyExistsException;
import org.example.postapi.domain.user.entity.AppUser;
import org.example.postapi.domain.user.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-15
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceImpl implements AppUserService{

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void createAppUser(String email, String password) {
        if(appUserRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }

        // User email id-part for initial nickname
        String nickname = email.split("@")[0];

        AppUser user = AppUser.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .build();
        user.addRole(Role.USER);
        user.setNickname(nickname);



        appUserRepository.save(user);

        log.info("New Local AppUser(email={}) registered", user.getEmail());
    }


    // Not inside Persistence Context
    private AppUser createUnregisteredUser(RegistrationProvider provider, String providedId) {
        return AppUser.builder().isSocial(true).registrationProvider(provider).providedId(providedId).build();
    }

    @Override
    @Transactional
    public AppUser createOrUpdateAppUser(RegistrationProvider provider, String providedId, String providedName, String providedEmail) {

        AppUser appUser = appUserRepository
            .findByRegistrationProviderAndProvidedId(provider, providedId)
            .orElseGet(() -> createUnregisteredUser(provider, providedId));

        appUser.setProvidedEmail(providedEmail);
        appUser.setProvidedName(providedName);


        // New user
        if(appUser.getId() == null){
            appUser.addRole(Role.USER);
            appUser.setNickname(providedName);
            log.info("New OAuth2.0 AppUser(provider={}, subject={}) registered", provider, providedId);
        }

        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser getAppUserByEmail(String email) {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new AppUserNotFoundException("Email not found :" + email));
    }

    @Override
    public AppUser getAppUserById(UUID id) {
        return appUserRepository.findById(id).orElseThrow(()->new AppUserNotFoundException("Id not found :" + id));
    }
}
