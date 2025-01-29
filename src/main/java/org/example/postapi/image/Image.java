package org.example.postapi.image;

import jakarta.persistence.*;
import lombok.*;
import org.example.postapi.common.entity.BaseEntity;
import org.example.postapi.user.entity.AppUser;

/**
 * @author rival
 * @since 2025-01-28
 */

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // key in s3
    @Column(nullable=false, unique = true)
    private String filename;

    @Column
    private String contentType;

    private Long size;

    private String originalName;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private AppUser owner;

}
