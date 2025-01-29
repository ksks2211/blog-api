package org.example.postapi.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author rival
 * @since 2025-01-28
 */
public interface StorageService {

    Resource download(String filename);
    void upload(MultipartFile file, String filename);
    void delete(String filename);
    String getUrl(String filename);

    void rename(String oldKey, String newKey);
}
