package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.OptionSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionSetRepository extends JpaRepository<OptionSet, Long> {
    Optional<OptionSet> findByCode(String code);
}
