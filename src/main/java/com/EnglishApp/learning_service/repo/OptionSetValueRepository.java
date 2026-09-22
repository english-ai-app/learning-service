package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.OptionSetValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionSetValueRepository extends JpaRepository<OptionSetValue, Long> {
    List<OptionSetValue> findByOptionSetId(Long optionSetId);

    Optional<OptionSetValue> findByOptionSetIdAndCode(Long optionSetId, String code);
}
