package org.example.postapi.domain.image;

import org.example.postapi.domain.user.entity.AppUser;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author rival
 * @since 2025-01-28
 */
public class ImageSpecifications {

    public static Specification<Image> isNotDeleted(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(Image_.DELETED));
    }

    public static Specification<Image> ownedBy(AppUser owner){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Image_.owner), owner);
    }
    
    
    public static Specification<Image> idEquals(Long id){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Image_.id), id);
    }


}
