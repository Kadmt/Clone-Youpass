package com.youpass.backend.repository.writing;

import com.youpass.backend.entity.WritingPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WritingPromptRepository extends JpaRepository<WritingPrompt, Long> {
}
