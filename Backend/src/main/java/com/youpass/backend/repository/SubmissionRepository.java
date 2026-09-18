package com.youpass.backend.repository;

import com.youpass.backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(Long userId);

    List<Submission> findByUserIdAndSkillType(Long userId, String skillType) ;
}
