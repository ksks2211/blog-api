package org.example.postapi.domain.post.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.postapi.domain.post.Post;
import org.example.postapi.domain.post.Post_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @author rival
 * @since 2025-02-03
 */
public class PostSearchRepositoryImpl implements PostSearchRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<PostPreviewDto> searchPosts(Specification<Post> spec, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PostPreviewDto> query = cb.createQuery(PostPreviewDto.class);


        query.distinct(true);

        Root<Post> root = query.from(Post.class);

        query.select(cb.construct(PostPreviewDto.class,
            root.get(Post_.ID),
            root.get(Post_.WRITER_ID),
            root.get(Post_.TITLE),
            root.get(Post_.DESCRIPTION),
            root.get(Post_.WRITER_NAME),
            root.get(Post_.CREATED_AT),
            root.get(Post_.UPDATED_AT)
        ));



        query.where(spec.toPredicate(root,query,cb));



        TypedQuery<PostPreviewDto> typedQuery = em.createQuery(query);

        // limit + offset (paging)
        typedQuery.setFirstResult((int)pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());


        List<PostPreviewDto> content = typedQuery.getResultList();


        long total = getCountQuery(spec).getSingleResult();



        return new PageImpl<>(content, pageable, total);
    }


    private TypedQuery<Long> getCountQuery(Specification<Post> spec){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.distinct(true);

        Root<Post> countRoot = countQuery.from(Post.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(spec.toPredicate(countRoot, countQuery, cb));

        return em.createQuery(countQuery);
    }
}
