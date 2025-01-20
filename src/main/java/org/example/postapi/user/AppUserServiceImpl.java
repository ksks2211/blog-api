package org.example.postapi.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.user.entity.AppUser;
import org.example.postapi.user.entity.Role;
import org.example.postapi.user.exception.AppUserNotFoundException;
import org.example.postapi.user.exception.EmailAlreadyExistsException;
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
        AppUser user = AppUser.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .build();
        user.addRole(Role.USER);
        appUserRepository.save(user);


        log.info("New Local AppUser(email={}) registered", user.getEmail());
    }

    @Override
    @Transactional
    public AppUser createOrUpdateAppUser(RegistrationProvider provider, String providedId, String providedName, String providedEmail) {

        AppUser appUser = appUserRepository.findByRegistrationProviderAndProvidedId(provider, providedId).orElseGet(
            () -> AppUser.builder().registrationProvider(provider).providedId(providedId).build());
        if(appUser.getId() == null){
            appUser.addRole(Role.USER);
            log.info("New OAuth2.0 AppUser(provider={}, subject={}) registered", provider, providedId);
        }

        appUser.setProvidedEmail(providedEmail);
        appUser.setProvidedName(providedName);

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
