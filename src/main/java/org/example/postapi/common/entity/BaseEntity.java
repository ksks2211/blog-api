package org.example.postapi.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * @author rival
 * @since 2025-01-15
 */
@MappedSuperclass
@EntityListeners(value= AuditingEntityListener.class)
@Getter
@Setter
@ToString
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column
    private Instant updatedAt;

    @Column
    private Instant deletedAt;


    @Column
    private boolean deleted = false;

    public void softDelete(){
        if(!deleted) {
            deleted = true;
            deletedAt = Instant.now();
        }
    }
}
