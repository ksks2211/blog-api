package org.example.postapi.domain.post.dto.req;

/**
 * @author rival
 * @since 2025-02-03
 */
public class PostValidationConstants {

    public static final String TAG_RULE_REGEX = "^\\w+( \\w+)*$";
    public static final String TAG_RULE_MESSAGE = "Only single spaces allowed between words";

    private PostValidationConstants() {}

}
