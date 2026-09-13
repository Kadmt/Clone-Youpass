package com.youpass.backend.repository.reading;

import com.youpass.backend.entity.ReadingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingQuestionRepository extends JpaRepository<ReadingQuestion, Long> {
}
