package com.openclassrooms.mddapi.comment.repository;

import com.openclassrooms.mddapi.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query(
      """
      select comment
      from Comment comment
      join fetch comment.author
      where comment.post.id = :postId
      order by comment.createdAt
      """)
  List<Comment> findAllByPostIdWithAuthor(@Param("postId") Long postId);
}
