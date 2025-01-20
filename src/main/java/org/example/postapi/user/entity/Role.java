package org.example.postapi.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author rival
 * @since 2025-01-15
 */
@RequiredArgsConstructor
@Getter
public enum Role {
    USER(EnumSet.of(Privilege.READ_POST)),
    ADMIN(EnumSet.allOf(Privilege.class));

    private final Set<Privilege> privileges;

    public String getPrefixedRole() {
        return "ROLE_" + this.name();
    }
}
