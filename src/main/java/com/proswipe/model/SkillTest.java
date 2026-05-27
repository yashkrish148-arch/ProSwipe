package com.proswipe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_tests")
public class SkillTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seeker_id", nullable = false)
    private Long seekerId;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    // Score out of 100
    @Column(nullable = false)
    private Integer score;

    // true if score >= 70
    @Column(nullable = false)
    private Boolean passed;

    @Column(name = "test_date")
    private LocalDateTime testDate;

    @PrePersist
    protected void onCreate() {
        testDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public LocalDateTime getTestDate() { return testDate; }
    public void setTestDate(LocalDateTime testDate) { this.testDate = testDate; }
}
