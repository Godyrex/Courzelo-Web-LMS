package org.example.courzelo.models.Forum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.courzelo.models.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
@Document(collection = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    private String id;
    private String text;
    @DBRef
    private Post post;

    @CreatedDate
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate = LocalDateTime.now();
    @DBRef
    private User user;

    private List<String> upvotedBy; // List of user IDs who upvoted
    private List<String> downvotedBy; // List of user IDs who downvoted
}