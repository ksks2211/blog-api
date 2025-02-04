package org.example.postapi.domain.post;

import jakarta.persistence.*;
import lombok.*;
import org.example.postapi.common.entity.BaseEntity;
import org.example.postapi.domain.postag.PostTag;
import org.example.postapi.domain.tag.Tag;
import org.example.postapi.domain.user.entity.AppUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;


    private String description;


    @Column(columnDefinition = "TEXT")
    private String content;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn
    @ToString.Exclude
    private AppUser author;


    private String writerName;

    private String writerId;



    @OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<PostTag> postTags = new ArrayList<>();


    public void addTag(Tag tag) {
        PostTag postTag = PostTag.builder().post(this).userId(this.writerId).tag(tag).build();
        this.postTags.add(postTag);
    }


    public void removeAllTags(){
        if(postTags != null && !postTags.isEmpty()){
            postTags.clear();
        }
    }


    public void removeTag(PostTag postTag){
        if(postTags!=null && postTags.contains(postTag)){
            this.postTags.remove(postTag);
        }
    }

}
