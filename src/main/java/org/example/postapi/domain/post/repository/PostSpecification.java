package org.example.postapi.domain.post.repository;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.postapi.domain.post.Post;
import org.example.postapi.domain.post.dto.PostSearchQuery;
import org.example.postapi.domain.post.Post_;
import org.example.postapi.domain.postag.PostTag;
import org.example.postapi.domain.postag.PostTag_;
import org.example.postapi.domain.tag.Tag;
import org.example.postapi.domain.user.entity.AppUser;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
public class PostSpecification {


    public static Specification<Post> isNotDeleted(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(Post_.DELETED));
    }


    public static Specification<Post> isNext(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(Post_.id), id);
    }

    public static Specification<Post> isPrev(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(Post_.id), id);
    }


    public static Specification<Post> wroteBy(AppUser author) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Post_.AUTHOR), author);
    }

    public static Specification<Post> idEquals(Long id){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Post_.id), id);
    }


    public static Specification<Post> haveTag(Tag tag) {
        return (root, query, criteriaBuilder) -> {
            var joined = root.join(Post_.POST_TAGS);
            return criteriaBuilder.equal(
                joined.get(PostTag_.TAG),
                tag
            );
        };
    }



    public static Specification<Post> haveAllTags(List<Tag> tags){

        return (root, query, criteriaBuilder)-> {
            if (tags == null || query == null || tags.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Subquery to count the matching tags for each post

            // post_tag.tag in [ ...tags ]
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<PostTag> subQueryRoot = subQuery.from(PostTag.class);
            Predicate tagsMatch = subQueryRoot.get(PostTag_.TAG).in(tags);

            // post_tag.post = post(of main query)
            Predicate postMatch = criteriaBuilder.equal(subQueryRoot.get(PostTag_.POST), root);

            // Count distinct tag from
            subQuery.select(criteriaBuilder.countDistinct(subQueryRoot.get(PostTag_.TAG)))
                .where(criteriaBuilder.and(tagsMatch, postMatch));



            return criteriaBuilder.equal(subQuery, (long)tags.size());
        };
    }


    public static Specification<Post> satisfiesSearchQuery(PostSearchQuery postSearchQuery) {
        Specification<Post> spec = Specification.where(null);

        if(postSearchQuery.getWriterId()!=null) {
            AppUser user = AppUser.builder().id(postSearchQuery.getWriterId()).build();
            spec = spec.and(wroteBy(user));
        }

        List<Tag> tags = postSearchQuery.getTags();
        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(haveAllTags(tags));
        }
        return spec;
    }



}
