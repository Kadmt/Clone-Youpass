package com.youpass.backend.entity;

public interface GradableQuestion {
    Long getId();
    String getCorrectAnswer();
    String getQuestionText();
    String getQuestionType();
    String getOptionsJson();
}
