package com.openclassrooms.mddapi.post.repository;

import com.openclassrooms.mddapi.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query(
      """
      select post
      from Post post
      join fetch post.author
      join fetch post.topic
      where post.topic.id in (
        select subscription.id.topicId
        from Subscription subscription
        where subscription.id.userId = :userId
      )
      """)
  List<Post> findAllForSubscribedTopics(@Param("userId") Long userId, Sort sort);

  @Query(
      """
      select post
      from Post post
      join fetch post.author
      join fetch post.topic
      where post.id = :postId
        and post.topic.id in (
          select subscription.id.topicId
          from Subscription subscription
          where subscription.id.userId = :userId
        )
      """)
  Optional<Post> findByIdForSubscribedTopic(
      @Param("postId") Long postId, @Param("userId") Long userId);
}
