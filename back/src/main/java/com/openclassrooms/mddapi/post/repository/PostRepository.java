package com.openclassrooms.mddapi.post.repository;

import com.openclassrooms.mddapi.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findAllByOrderByCreatedAtDesc();

  List<Post> findAllByOrderByCreatedAtAsc();
}
