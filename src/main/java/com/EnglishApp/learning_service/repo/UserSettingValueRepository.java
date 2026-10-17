package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.UserSettingValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserSettingValueRepository extends JpaRepository<UserSettingValue, Long> {

    List<UserSettingValue> findByUserId(Long userId);

    Optional<UserSettingValue> findByUserIdAndOptionSetValueId(Long userId, Long optionSetValueId);

    List<UserSettingValue> findByUserIdAndOptionSetValue_IdIn(Long userId, Collection<Long> optionSetValueIds);

    boolean existsByUserIdAndOptionSetValueId(Long userId, Long optionSetValueId);

    void deleteByUserIdAndOptionSetValueId(Long userId, Long optionSetValueId);

    @Modifying
    @Query("DELETE FROM UserSettingValue u WHERE u.userId = :userId AND u.optionSetValue.id IN :optionSetValueIds")
    void deleteByUserIdAndOptionSetValueIdIn(@Param("userId") Long userId, @Param("optionSetValueIds") Collection<Long> optionSetValueIds);
}
