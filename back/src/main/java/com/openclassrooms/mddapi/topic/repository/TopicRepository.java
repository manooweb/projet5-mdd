package com.openclassrooms.mddapi.topic.repository;

import com.openclassrooms.mddapi.topic.domain.Topic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {

  List<Topic> findAllByOrderByIdAsc();
}
