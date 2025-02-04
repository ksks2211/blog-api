package org.example.postapi.domain.post;

import org.example.postapi.common.config.JpaConfig;
import org.example.postapi.domain.post.repository.PostPreviewDto;
import org.example.postapi.domain.post.repository.PostSpecification;
import org.example.postapi.domain.post.repository.PostRepository;
import org.example.postapi.domain.tag.Tag;
import org.example.postapi.domain.user.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rival
 * @since 2025-02-03
 */

@DataJpaTest
@Import(JpaConfig.class)
class PostRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    PostRepository postRepository;

    AppUser createAppUser(){
        String email = "user123@example.com";
        String password ="password123";
        String nickname = "user123";
        AppUser user = AppUser.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .build();
        AppUser persist = entityManager.persist(user);
        entityManager.flush();
        return persist;
    }

    Tag createTag(String tagValue){
        Tag tag = new Tag(tagValue);
        Tag persist = entityManager.persist(tag);
        entityManager.flush();
        return persist;
    }


    Post createPost(AppUser writer, String title, String content, String description){
        String writerName = writer.getNickname();
        return Post.builder()
            .author(writer)
            .title(title)
            .writerId(writer.getId().toString())
            .content(content)
            .writerName(writerName)
            .description(description).build();
    }

    @Test
    public void test_create_post(){
        AppUser author = createAppUser();
        Tag java = createTag("java");
        Tag python = createTag("python");
        Tag javascript = createTag("javascript");

        String title = "post title";
        String content = "post content";
        String description = "post description";
        String writerName = author.getNickname();
        Post post = Post.builder()
            .author(author)
            .title(title)
            .content(content)
            .writerName(writerName)
            .description(description).build();
        post.addTag(java);
        post.addTag(python);
        post.addTag(javascript);

        Post savedPost = postRepository.save(post);
        System.out.println(savedPost);

        assertEquals(title, savedPost.getTitle());
        assertEquals(writerName, savedPost.getWriterName());
        assertEquals(description, savedPost.getDescription());

        entityManager.flush();
        entityManager.clear();

        Post foundPost = postRepository.findById(savedPost.getId()).orElseThrow();
        System.out.println(foundPost);

        assertEquals(savedPost.getTitle(),foundPost.getTitle());
        assertEquals(savedPost.getWriterName(), foundPost.getWriterName());
        assertEquals(savedPost.getDescription(), foundPost.getDescription());
        assertEquals(3, foundPost.getPostTags().size()); // ref : tags
        assertEquals(author.getId(), foundPost.getAuthor().getId()); // ref : writer
    }

    @Test
    public void test_delete_post(){
        AppUser author = createAppUser();
        String title = "post title";
        String content = "post content";
        String description = "post description";
        String writerName = author.getNickname();
        Post post = Post.builder()
            .author(author)
            .title(title)
            .content(content)
            .writerName(writerName)
            .description(description).build();

        Post savedPost = postRepository.save(post);
        System.out.println(savedPost);
        entityManager.flush();

        savedPost.softDelete();
        postRepository.save(savedPost);
        entityManager.flush();


        Optional<Post> optionalPost = postRepository.findById(savedPost.getId());
        assertTrue(optionalPost.isEmpty());
    }





    @Test
    public void test_specifications_2(){
        AppUser appUser = createAppUser();
        String title = "post title";
        String content = "post content";
        String description = "post description";
        Post post = createPost(appUser, title, content, description);
        postRepository.save(post);
        System.out.println(post);
        entityManager.flush();


        Specification<Post> spec = PostSpecification.wroteBy(appUser);

        List<Post> posts = postRepository.findAll(spec);

        assertEquals(1, posts.size());
        posts.forEach(p-> assertEquals(appUser.getId(), p.getAuthor().getId()));
    }


    @Test
    public void test_specifications_3(){
        AppUser appUser = createAppUser();
        Tag java = createTag("java");
        Tag python = createTag("python");

        String title = "post title";
        String content = "post content";
        String description = "post description";

        Post post = createPost(appUser, title, content, description);
        post.addTag(java);
        post.addTag(python);
        postRepository.save(post);
        System.out.println(post);
        entityManager.flush();


        Specification<Post> spec = PostSpecification.haveTag(java);
        List<Post> posts = postRepository.findAll(spec);

        assertEquals(1, posts.size());
        posts.forEach(p-> assertEquals(appUser.getId(), p.getAuthor().getId()));
    }



    @Test
    public void test_specifications_4(){
        AppUser appUser = createAppUser();
        Tag java = createTag("java");
        Tag python = createTag("python");
        Tag javascript = createTag("javascript");

        String title = "post title";
        String content = "post content";
        String description = "post description";

        Post post = createPost(appUser, title, content, description);
        post.addTag(java);
        post.addTag(python);
        postRepository.save(post);
        System.out.println(post);
        entityManager.flush();


        Specification<Post> spec = PostSpecification.haveAllTags(List.of(java, python));
        List<Post> posts = postRepository.findAll(spec);

        assertEquals(1, posts.size());
        posts.forEach(p-> assertEquals(appUser.getId(), p.getAuthor().getId()));

        Specification<Post> spec2 = PostSpecification.haveAllTags(List.of(java, python, javascript));
        List<Post> posts2 = postRepository.findAll(spec2);

        assertTrue(posts2.isEmpty());
    }



    @Test
    public void test_projection(){
        AppUser appUser = createAppUser();
        Post post1 = createPost(appUser, "title 1", "content 1", "description 1");
        Post post2 = createPost(appUser, "title 2", "content 2", "description 2");
        postRepository.saveAll(List.of(post1, post2));


        PageRequest pageRequest = PageRequest.of(0, 10);



        Page<PostPreviewDto> page = postRepository.findAllPreview(pageRequest);
        assertFalse(page.isEmpty());


        List<PostPreviewDto> postPreviews = page.getContent();
        assertEquals(2, postPreviews.size());
    }

    @Test
    public void test_projection2(){
        AppUser appUser = createAppUser();
        Tag java = createTag("java");
        Post post1 = createPost(appUser, "title 1", "content 1", "description 1");
        Post post2 = createPost(appUser, "title 2", "content 2", "description 2");
        post2.addTag(java);
        postRepository.saveAll(List.of(post1, post2));

        Specification<Post> spec = PostSpecification.haveTag(java);


        Page<PostPreviewDto> postPreviewDtos = postRepository.searchPostPage(spec, PageRequest.of(0, 10));

        System.out.println(postPreviewDtos);
        System.out.println(postPreviewDtos.getContent());

    }



    @Test
    public void test_next_prev(){
        AppUser appUser = createAppUser();
        Post post1 = createPost(appUser, "title 1", "content 1", "description 1");
        Post post2 = createPost(appUser, "title 2", "content 2", "description 2");
        postRepository.saveAll(List.of(post1, post2));


        List<Post> posts = postRepository.findAll();


        Post firstOne = posts.get(0);
        Post secondOne = posts.get(1);

        System.out.println("=====================================================================");
        PostPreviewDto secondOnePreview = postRepository.findNextPost(firstOne.getId()).get();
        PostPreviewDto firstOnePreview = postRepository.findPrevPost(secondOne.getId()).get();


        assertEquals(secondOne.getTitle(), secondOnePreview.getTitle());
        assertEquals(firstOne.getTitle(), firstOnePreview.getTitle());





    }




    @Test
    void test_soft_delete(){
        AppUser appUser = createAppUser();
        Post post = createPost(appUser, "title 1", "content 1", "description 1");
        post = postRepository.save(post);
        Long postId = post.getId();
        entityManager.flush();


        postRepository.softDelete(postId);
        entityManager.flush();
        entityManager.clear();


        Optional<Post> optionalPost = postRepository.findById(postId);
        assertTrue(optionalPost.isEmpty());

    }



}