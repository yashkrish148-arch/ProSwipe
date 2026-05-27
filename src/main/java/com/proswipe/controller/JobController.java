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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired private JobRepository jobRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ApplicationRepository applicationRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findByStatus("active"));
    }

    // REQ-F-16 to F-19: Skill-based recommendation engine
    @GetMapping("/recommended/{userId}")
    public ResponseEntity<?> getRecommended(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();
        String skillsStr = user.getSkills() != null ? user.getSkills().toLowerCase() : "";
        Set<String> userSkills = Arrays.stream(skillsStr.split("[,;\\s]+"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // REQ-F-18: Exclude all already-viewed/applied jobs
        Set<Long> viewedJobIds = applicationRepository.findBySeekerId(userId)
                .stream().map(Application::getJobId).collect(Collectors.toSet());

        List<Map<String, Object>> scored = jobRepository.findByStatus("active")
                .stream()
                .filter(job -> !viewedJobIds.contains(job.getId()))
                .map(job -> {
                    String reqStr = job.getRequiredSkills() != null ? job.getRequiredSkills().toLowerCase() : "";
                    Set<String> reqSkills = Arrays.stream(reqStr.split("[,;\\s]+"))
                            .map(String::trim).filter(s -> !s.isEmpty())
                            .collect(Collectors.toSet());

                    double matchScore = 0;
                    if (!reqSkills.isEmpty()) {
                        long overlap = userSkills.stream().filter(reqSkills::contains).count();
                        matchScore = (double) overlap / reqSkills.size() * 100;
                    }

                    Map<String, Object> m = new HashMap<>();
                    m.put("id", job.getId());
                    m.put("title", job.getTitle());
                    m.put("company", job.getCompany());
                    m.put("description", job.getDescription());
                    m.put("requiredSkills", job.getRequiredSkills());
                    m.put("salaryRange", job.getSalaryRange());
                    m.put("location", job.getLocation());
                    m.put("status", job.getStatus());
                    m.put("deadline", job.getDeadline());
                    m.put("postedBy", job.getPostedBy());
                    m.put("createdAt", job.getCreatedAt());
                    m.put("matchScore", (int) Math.round(matchScore));
                    return m;
                })
                .sorted((a, b) -> Integer.compare((int) b.get("matchScore"), (int) a.get("matchScore")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(scored);
    }

    // REQ-F-33, F-34: Post job with all required fields including deadline
    @PostMapping("/post")
    public ResponseEntity<?> postJob(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("title") || !body.containsKey("company") || !body.containsKey("description"))
            return ResponseEntity.badRequest().body(Map.of("error", "Title, company and description are required"));

        Job job = new Job();
        job.setTitle(body.get("title").toString());
        job.setCompany(body.get("company").toString());
        job.setDescription(body.get("description").toString());
        job.setRequiredSkills(body.getOrDefault("requiredSkills", "").toString());
        job.setSalaryRange(body.getOrDefault("salaryRange", "").toString());
        job.setLocation(body.getOrDefault("location", "").toString());
        job.setStatus("active");
        if (body.containsKey("postedBy") && body.get("postedBy") != null)
            job.setPostedBy(Long.parseLong(body.get("postedBy").toString()));
        if (body.containsKey("deadline") && body.get("deadline") != null && !body.get("deadline").toString().isBlank())
            job.setDeadline(LocalDate.parse(body.get("deadline").toString()));

        return ResponseEntity.ok(jobRepository.save(job));
    }

    @GetMapping("/by-recruiter/{recruiterId}")
    public ResponseEntity<?> getByRecruiter(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(jobRepository.findByPostedBy(recruiterId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return jobRepository.findById(id).map(job -> {
            job.setStatus(body.get("status"));
            return ResponseEntity.ok(jobRepository.save(job));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        if (!jobRepository.existsById(id)) return ResponseEntity.notFound().build();
        jobRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }
}
