package org.example.postapi.domain.tag;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author rival
 * @since 2025-02-03
 */
@Entity
@Getter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String tagValue;


    public Tag(String tagValue){
        this.tagValue = tagValue;
    }

}
