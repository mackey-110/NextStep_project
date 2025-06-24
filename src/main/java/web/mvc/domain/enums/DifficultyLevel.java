package web.mvc.domain;

import lombok.Getter;

/**
 * 난이도 레벨 열거형
 */
@Getter
public enum DifficultyLevel {
    
    BEGINNER("초급", 1),
    INTERMEDIATE("중급", 2), 
    ADVANCED("고급", 3);
    
    private final String description;
    private final int level;
    
    DifficultyLevel(String description, int level) {
        this.description = description;
        this.level = level;
    }
}

/**
 * 학습자 현재 레벨
 */
@Getter  
enum CurrentLevel {
    
    COMPLETE_BEGINNER("완전 초보자"),
    BASIC_COMPLETED("기초 완료"),
    PROJECT_EXPERIENCE("프로젝트 경험"),
    WORK_EXPERIENCE("실무 경험");
    
    private final String description;
    
    CurrentLevel(String description) {
        this.description = description;
    }
}

/**
 * 콘텐츠 타입
 */
@Getter
enum ContentType {
    
    COURSE("강의"),
    ARTICLE("아티클"),
    TOOL("도구"),
    PROJECT("프로젝트"),
    BOOK("도서");
    
    private final String description;
    
    ContentType(String description) {
        this.description = description;
    }
}
