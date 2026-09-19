package com.youpass.backend.repository.reading;

import com.youpass.backend.entity.ReadingPassage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReadingPassageRepository extends JpaRepository<ReadingPassage, Long> {

    // các hàm cơ bản có sẵn

    // tìm test group theo Id
    public List<ReadingPassage> findByTestGroupId(Long testGroupId);

}
