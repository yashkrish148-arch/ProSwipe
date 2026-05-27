package com.proswipe.controller;

import com.proswipe.model.Application;
import com.proswipe.model.Job;
import com.proswipe.model.User;
import com.proswipe.repository.ApplicationRepository;
import com.proswipe.repository.JobRepository;
import com.proswipe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private UserRepository userRepository;

    // REQ-F-13: Persist all swipe actions
    @PostMapping("/swipe")
    public ResponseEntity<?> swipe(@RequestBody Map<String, Object> body) {
        Long seekerId = Long.parseLong(body.get("seekerId").toString());
        Long jobId = Long.parseLong(body.get("jobId").toString());
        String action = body.get("action").toString();

        Optional<Application> existing = applicationRepository.findBySeekerIdAndJobId(seekerId, jobId);
        if (existing.isPresent())
            return ResponseEntity.badRequest().body(Map.of("error", "Already swiped on this job"));

        Application app = new Application();
        app.setSeekerId(seekerId);
        app.setJobId(jobId);
        app.setAction(action);
        app.setStatus("like".equals(action) ? "pending" : "skipped");
        return ResponseEntity.ok(applicationRepository.save(app));
    }

    // REQ-F-45, F-47: Dashboard — all apps with match %, company, date
    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<?> getSeekerApplications(@PathVariable Long seekerId) {
        Optional<User> userOpt = userRepository.findById(seekerId);
        String userSkillsStr = userOpt.map(u -> u.getSkills() != null ? u.getSkills().toLowerCase() : "").orElse("");
        Set<String> userSkills = Arrays.stream(userSkillsStr.split("[,;\\s]+"))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        List<Application> apps = applicationRepository.findBySeekerId(seekerId);
        List<Map<String, Object>> result = apps.stream().map(app -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("seekerId", app.getSeekerId());
            map.put("jobId", app.getJobId());
            map.put("status", app.getStatus());
            // REQ-F-43, F-44: include feedback fields in dashboard
            map.put("feedbackReason", app.getFeedbackReason());
            map.put("feedback", app.getFeedback());
            map.put("appliedDate", app.getAppliedDate());
            map.put("action", app.getAction());

            jobRepository.findById(app.getJobId()).ifPresent(job -> {
                map.put("jobTitle", job.getTitle());
                map.put("company", job.getCompany());
                map.put("location", job.getLocation());
                map.put("salaryRange", job.getSalaryRange());
                map.put("deadline", job.getDeadline());

                // REQ-F-47: include match % per application
                String reqStr = job.getRequiredSkills() != null ? job.getRequiredSkills().toLowerCase() : "";
                Set<String> reqSkills = Arrays.stream(reqStr.split("[,;\\s]+"))
                        .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
                int matchScore = 0;
                if (!reqSkills.isEmpty()) {
                    long overlap = userSkills.stream().filter(reqSkills::contains).count();
                    matchScore = (int) Math.round((double) overlap / reqSkills.size() * 100);
                }
                map.put("matchScore", matchScore);
            });
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // REQ-F-35: Recruiter views applicants per job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJobApplicants(@PathVariable Long jobId) {
        List<Application> apps = applicationRepository.findByJobId(jobId);
        Optional<Job> jobOpt = jobRepository.findById(jobId);

        Set<String> reqSkills = jobOpt.map(j -> {
            String r = j.getRequiredSkills() != null ? j.getRequiredSkills().toLowerCase() : "";
            return Arrays.stream(r.split("[,;\\s]+")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        }).orElse(Collections.emptySet());

        List<Map<String, Object>> result = apps.stream()
                .filter(a -> "like".equals(a.getAction()))
                .map(app -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", app.getId());
                    map.put("seekerId", app.getSeekerId());
                    map.put("jobId", app.getJobId());
                    map.put("status", app.getStatus());
                    map.put("feedbackReason", app.getFeedbackReason());
                    map.put("feedback", app.getFeedback());
                    map.put("appliedDate", app.getAppliedDate());

                    userRepository.findById(app.getSeekerId()).ifPresent(user -> {
                        map.put("seekerName", user.getName());
                        map.put("seekerEmail", user.getEmail());
                        map.put("seekerSkills", user.getSkills());

                        // Calculate match score
                        String uskills = user.getSkills() != null ? user.getSkills().toLowerCase() : "";
                        Set<String> uSet = Arrays.stream(uskills.split("[,;\\s]+"))
                                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
                        int ms = 0;
                        if (!reqSkills.isEmpty()) {
                            long overlap = uSet.stream().filter(reqSkills::contains).count();
                            ms = (int) Math.round((double) overlap / reqSkills.size() * 100);
                        }
                        map.put("matchScore", ms);
                    });
                    return map;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // REQ-F-37, F-40 to F-44: Update status + full feedback with reason category
    @PutMapping("/status/{appId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long appId, @RequestBody Map<String, String> body) {
        return applicationRepository.findById(appId).map(app -> {
            if (body.containsKey("status")) app.setStatus(body.get("status"));
            // REQ-F-41: reason category stored separately
            if (body.containsKey("feedbackReason")) app.setFeedbackReason(body.get("feedbackReason"));
            // REQ-F-42: free-text explanation
            if (body.containsKey("feedback")) app.setFeedback(body.get("feedback"));
            applicationRepository.save(app);
            return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
