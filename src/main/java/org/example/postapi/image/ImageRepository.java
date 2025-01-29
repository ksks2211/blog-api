package org.example.postapi.image;

import io.lettuce.core.dynamic.annotation.Param;
import io.micrometer.common.lang.NonNull;
import org.example.postapi.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author rival
 * @since 2025-01-28
 */
public interface ImageRepository extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {

    boolean existsByFilename(String filename);

    boolean existsByFilenameAndOwner(@Param("filename") String filename, @Param("owner") AppUser owner);

    boolean existsByIdAndOwner(@Param("id") Long id, @Param("owner") AppUser owner);





    @Override
    @Query("SELECT i FROM Image  i where i.deleted = false and i.id = :id")
    @NonNull Optional<Image> findById(@NonNull @Param("id") Long id);

}
