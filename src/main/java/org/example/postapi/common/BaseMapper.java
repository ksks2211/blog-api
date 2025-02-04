package org.example.postapi.common;

import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author rival
 * @since 2025-01-30
 */
public interface BaseMapper {

    @Named("toISODateTime")
    default String toISODateTime(Instant createdAt) {
        return createdAt.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Named("zeroBaseToOneBasePage")
    default int zeroBaseToOneBasePage(int zeroBasedPageNum) {
        return zeroBasedPageNum+1;
    }

}
