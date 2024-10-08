package org.example.courzelo.Communication;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.example.courzelo.models.Forum.Comment;
import org.example.courzelo.models.Forum.Post;
import org.example.courzelo.repositories.Forum.CommentRepository;
import org.example.courzelo.serviceImpls.Forum.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private Post post;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        post = new Post();
        post.setId("1");

        comment = new Comment();
        comment.setId("1");
        comment.setText("Test Comment");
        comment.setPost(post);
    }

    @Test
    public void testCreateComment() {
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment savedComment = commentService.saveComment(comment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Test Comment");
        verify(commentRepository).save(comment);
    }

    @Test
    public void testGetCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();

        comments.add(comment);
        when(commentRepository.findByPost(post)).thenReturn(comments);

        List<Comment> foundComments = commentService.getCommentByPost("1");

        assertThat(foundComments).hasSize(1);
        assertThat(foundComments.get(0).getText()).isEqualTo("Test Comment");
        verify(commentRepository).findByPost(post);
    }

    @Test
    public void testDeleteComment() {
        doNothing().when(commentRepository).deleteById("1");

        commentService.deleteComment("1");

        verify(commentRepository).deleteById("1");
    }
}

