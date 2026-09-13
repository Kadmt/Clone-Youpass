package com.youpass.backend.repository.listening;

import com.youpass.backend.entity.ListeningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningQuestionRepository extends JpaRepository<ListeningQuestion, Long>{

}
