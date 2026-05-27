package com.proswipe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seeker_id", nullable = false)
    private Long seekerId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    // REQ-F-46: pending, reviewing, shortlisted, accepted, rejected
    @Column(nullable = false)
    private String status = "pending";

    // REQ-F-41: reason category (skill_gap | experience | profile_incomplete | position_filled | other)
    @Column(name = "feedback_reason")
    private String feedbackReason;

    // REQ-F-42: optional free-text detailed explanation
    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    // "like" = applied, "skip" = skipped
    @Column(nullable = false)
    private String action;

    // Transient fields for enriched API responses
    @Transient private String jobTitle;
    @Transient private String company;
    @Transient private String location;
    @Transient private String salaryRange;
    @Transient private Integer matchScore;
    @Transient private String seekerName;
    @Transient private String seekerEmail;
    @Transient private String seekerSkills;

    @PrePersist
    protected void onCreate() {
        appliedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSeekerId() { return seekerId; }
    public void setSeekerId(Long seekerId) { this.seekerId = seekerId; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFeedbackReason() { return feedbackReason; }
    public void setFeedbackReason(String feedbackReason) { this.feedbackReason = feedbackReason; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }

    public String getSeekerEmail() { return seekerEmail; }
    public void setSeekerEmail(String seekerEmail) { this.seekerEmail = seekerEmail; }

    public String getSeekerSkills() { return seekerSkills; }
    public void setSeekerSkills(String seekerSkills) { this.seekerSkills = seekerSkills; }
}
