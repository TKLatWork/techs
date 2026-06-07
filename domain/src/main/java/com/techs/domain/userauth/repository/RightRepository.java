package com.techs.domain.userauth.repository;

import com.techs.domain.userauth.entity.Right;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RightRepository {
    Right save(Right right);
    Optional<Right> findById(String id);
    Optional<Right> findByName(String name);
    List<Right> findAll();
    List<Right> findByFeatureId(String featureId);
    Set<String> findIdsByFeatureId(String featureId);
    void deleteById(String id);
    void deleteByFeatureId(String featureId);
}
