package org.example.postapi.refresh;

import jakarta.persistence.*;
import lombok.*;
import org.example.postapi.user.entity.AppUser;

import java.time.Instant;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-21
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column
    private Instant expiresAt;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    private AppUser user;


    public boolean isExpired(){
        return Instant.now().isAfter(expiresAt);
    }
}
