package org.example.postapi.user;

import org.example.postapi.common.config.JpaConfig;
import org.example.postapi.domain.user.AppUserRepository;
import org.example.postapi.domain.user.entity.AppUser;
import org.example.postapi.domain.user.entity.Role;
import org.example.postapi.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rival
 * @since 2025-01-15
 */

@DataJpaTest
@Import(JpaConfig.class)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreateUser() {

        // Given
        String email = "test@test.com";
        String password = "test";
        AppUser user = AppUser.builder()
            .email(email)
            .password(password)
            .build();
        user.addRole(Role.USER);

        // When
        AppUser newUser = appUserRepository.save(user);
        UUID id = newUser.getId();
        entityManager.flush();
        AppUser foundUser = appUserRepository.findById(id).orElseThrow();
        List<UserRole> roles = foundUser.getRoles();


        // Then
        assertNotNull(newUser);
        assertEquals(email, newUser.getEmail());
        assertEquals(password, newUser.getPassword());
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals(Role.USER, roles.get(0).getRole());


        System.out.println("=== User : " + newUser);
        System.out.println("=== User Roles : " + roles);
    }



    @Test
    public void testRemoveRole(){

        // Given
        String email = "test@test.com";
        String password = "test";
        AppUser user = AppUser.builder()
            .email(email)
            .password(password)
            .build();
        user.addRole(Role.USER);
        AppUser newUser = appUserRepository.save(user);
        UUID id = newUser.getId();
        entityManager.flush();


        // When
        AppUser foundUser = appUserRepository.findById(id).orElseThrow();
        foundUser.removeRole(Role.USER);
        appUserRepository.save(foundUser);
        entityManager.flush();
        AppUser roleRemovedUser = appUserRepository.findById(id).orElseThrow();
        List<UserRole> roles = roleRemovedUser.getRoles();

        // Then
        assertNotNull(roleRemovedUser);
        assertNotNull(roleRemovedUser.getRoles());
        assertEquals(0,roles.size());



        System.out.println("=== User : " + roleRemovedUser);
        System.out.println("=== User Roles : " + roles);

    }
}