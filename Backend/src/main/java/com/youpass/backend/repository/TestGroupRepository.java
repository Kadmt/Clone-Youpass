package com.youpass.backend.repository;

import com.youpass.backend.dto.response.TestGroupForReading;
import com.youpass.backend.entity.TestGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TestGroupRepository extends JpaRepository<TestGroup, Long> {
    public List<TestGroup> findBySkillType(String skillType);

}
