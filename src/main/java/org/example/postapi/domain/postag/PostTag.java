package org.example.postapi.domain.postag;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.postapi.domain.post.Post;
import org.example.postapi.domain.tag.Tag;

/**
 * @author rival
 * @since 2025-02-03
 */

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    indexes = {@Index(columnList = PostTagConstants.COL_USER_ID)}
)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Post post;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    private Tag tag;

    @Column(name=PostTagConstants.COL_USER_ID)
    private String userId;
}
