package org.example.postapi.domain.post.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.example.postapi.domain.post.Post;
import org.example.postapi.domain.post.Post_;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author rival
 * @since 2025-02-03
 */
public class PostSearchRepositoryImpl implements PostSearchRepository {

    @PersistenceContext
    private EntityManager em;





    @Override
    public Page<PostPreviewDto> searchPostPage(Specification<Post> spec, Pageable pageable) {

        var typedQuery = getPostsQuery(spec, pageable);

        // limit + offset (paging)
        typedQuery.setFirstResult((int)pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());


        List<PostPreviewDto> content = typedQuery.getResultList();
        long total = getCountQuery(spec).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<PostPreviewDto> searchPostList(Specification<Post> spec, Pageable pageable) {

        var typedQuery = getPostsQuery(spec, pageable);
        int pageSize = pageable.getPageSize();

        // limit + offset (paging)
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();
    }


    public TypedQuery<PostPreviewDto> getPostsQuery(Specification<Post> spec, Pageable pageable){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PostPreviewDto> query = cb.createQuery(PostPreviewDto.class);
        Root<Post> root = query.from(Post.class);
        List<Order> orders = getOrderList(root, cb, pageable);

        query.orderBy(orders);
        query.distinct(true);

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
        return em.createQuery(query);
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


    private List<Order> getOrderList(Root<Post> root,CriteriaBuilder cb,Pageable pageable){
        return pageable.getSort().stream().map(order -> {
            var path = root.get(order.getProperty());
            return order.isAscending() ? cb.asc(path) : cb.desc(path);
        }).toList();
    }
}
