package com.proswipe.repository;

import com.proswipe.model.SkillTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillTestRepository extends JpaRepository<SkillTest, Long> {
    List<SkillTest> findBySeekerId(Long seekerId);
    List<SkillTest> findBySeekerIdAndSkillName(Long seekerId, String skillName);
    Optional<SkillTest> findTopBySeekerIdAndSkillNameOrderByTestDateDesc(Long seekerId, String skillName);
}
