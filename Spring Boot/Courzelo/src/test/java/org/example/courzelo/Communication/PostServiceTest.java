package org.example.courzelo.Communication;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.example.courzelo.models.Forum.Post;
import org.example.courzelo.models.Forum.SubForum;
import org.example.courzelo.repositories.Forum.PostRepository;
import org.example.courzelo.repositories.Forum.SubforumRepository;
import org.example.courzelo.serviceImpls.Forum.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private SubforumRepository subforumRepository;
    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private SubForum subForum;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subForum = new SubForum();
        subForum.setId("1");

        post = new Post();
        post.setId("1");
        post.setPostName("Test Post");
        post.setSubforum(subForum);
    }

    @Test
    public void testCreatePost() {
        when(postRepository.save(post)).thenReturn(post);

        Post savedPost = postService.savePost(post);

        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getPostName()).isEqualTo("Test Post");
        verify(postRepository).save(post);
    }

    @Test
    public void testGetPostById() {
        when(postRepository.findById("1")).thenReturn(Optional.of(post));

        Post foundPost = postService.getPost("1");

        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getId()).isEqualTo("1");
        verify(postRepository).findById("1");
    }

    @Test
    public void testGetPostsBySubForumId() {
        List<Post> posts = new ArrayList<>();
        SubForum subForum1 = subforumRepository.findById("1").get();
        posts.add(post);
        when(postRepository.findBySubforum(subForum1)).thenReturn(posts);

        List<Post> foundPosts = postService.getPostsBySub("1");

        assertThat(foundPosts).hasSize(1);
        assertThat(foundPosts.get(0).getPostName()).isEqualTo("Test Post");
        verify(postRepository).findBySubforum(subForum1);
    }

    @Test
    public void testDeletePost() {
        doNothing().when(postRepository).deleteById("1");

        postService.deletePost("1");

        verify(postRepository).deleteById("1");
    }
}
