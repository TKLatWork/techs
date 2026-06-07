package com.techs.app.repository;

import com.techs.domain.userauth.entity.Right;
import com.techs.domain.userauth.repository.RightRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryRightRepository implements RightRepository {

    private final ConcurrentHashMap<String, Right> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> nameIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> featureIdIndex = new ConcurrentHashMap<>();

    @Override
    public Right save(Right right) {
        store.put(right.getId(), right);
        nameIndex.put(right.getName(), right.getId());
        if (right.getFeatureId() != null) {
            featureIdIndex.computeIfAbsent(right.getFeatureId(), k -> ConcurrentHashMap.newKeySet())
                    .add(right.getId());
        }
        return right;
    }

    @Override
    public Optional<Right> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Right> findByName(String name) {
        String id = nameIndex.get(name);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Right> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Right> findByFeatureId(String featureId) {
        Set<String> ids = featureIdIndex.getOrDefault(featureId, Set.of());
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> findIdsByFeatureId(String featureId) {
        return new HashSet<>(featureIdIndex.getOrDefault(featureId, Set.of()));
    }

    @Override
    public void deleteById(String id) {
        Right right = store.remove(id);
        if (right != null) {
            nameIndex.remove(right.getName());
            if (right.getFeatureId() != null) {
                Set<String> ids = featureIdIndex.get(right.getFeatureId());
                if (ids != null) {
                    ids.remove(id);
                    if (ids.isEmpty()) {
                        featureIdIndex.remove(right.getFeatureId());
                    }
                }
            }
        }
    }

    @Override
    public void deleteByFeatureId(String featureId) {
        Set<String> ids = featureIdIndex.remove(featureId);
        if (ids != null) {
            for (String id : ids) {
                Right right = store.remove(id);
                if (right != null) {
                    nameIndex.remove(right.getName());
                }
            }
        }
    }
}
