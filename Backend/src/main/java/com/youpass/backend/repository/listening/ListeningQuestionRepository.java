package com.youpass.backend.repository.listening;

import com.youpass.backend.entity.ListeningQuestion;
import com.youpass.backend.entity.ListeningTrack;
import com.youpass.backend.entity.ReadingPassage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningQuestionRepository extends JpaRepository<ListeningQuestion, Long>{

}
