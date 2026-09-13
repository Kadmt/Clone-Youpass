package com.youpass.backend.repository;

import com.youpass.backend.entity.VocabularyEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyEntryRepository extends JpaRepository<VocabularyEntry, Long> {
}
