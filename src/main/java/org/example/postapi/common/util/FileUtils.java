package org.example.postapi.common.util;

import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author rival
 * @since 2025-01-28
 */
public class FileUtils {


    private FileUtils(){}

    /**
     * Generate random filename
     *
     * @param prefix prefix of filename
     * @return filename e.g. "{prefix}/2025/01/14/{uuid}"
     */
    public static String generateRandomFilename(final String prefix) {
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());
        String day = String.format("%02d", currentDate.getDayOfMonth());
        String uuid = UUID.randomUUID().toString();

        return String.format("%s/%s/%s/%s/%s", prefix, year, month, day, uuid);
    }


    public static boolean isImageContentType(MultipartFile uploadFile){
        return uploadFile !=null && uploadFile.getContentType() != null && uploadFile.getContentType().startsWith("image");
    }



    public static String getExtensionFromMimeType(String mimeType) {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        try {
            return allTypes.forName(mimeType).getExtension();
        } catch (MimeTypeException ignored) {
        }
        // Return a default extension if the actual extension can't be determined
        return ".unknown";
    }
}
