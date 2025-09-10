package com.local;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.ports.IssueRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryIssueRepository implements IssueRepository {

    private final Map<String, Issue> store = new HashMap<>();

    public InMemoryIssueRepository() {
        Issue issue1 = new Issue("AD-1", "Bug in login", "", Status.OPEN, Instant.now(), Instant.now());
        Issue issue2 = new Issue("AD-2", "Export crashes", "AD-1",Status.IN_PROGRESS, Instant.now(), Instant.now());
        Issue issue3 = new Issue("AD-3", "UI alignment issue","AD-1", Status.CLOSED, Instant.now(), Instant.now());

        store.put(issue1.getId(), issue1);
        store.put(issue2.getId(), issue2);
        store.put(issue3.getId(), issue3);
    }

    @Override
    public Issue create(Issue issue) {
        if (store.containsKey(issue.getId())) {
            throw new IllegalStateException("Issue with ID " + issue.getId() + " already exists");
        }
        store.put(issue.getId(), issue);
        return issue;
    }

    @Override
    public Optional<Issue> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Issue updateStatus(String id, Status newStatus, Instant updatedAt) {
        Issue existing = store.get(id);
        if (existing == null) {
            throw new NoSuchElementException("Issue not found: " + id);
        }
        existing.setStatus(newStatus);
        existing.setUpdatedAt(updatedAt);
        return existing;
    }

    @Override
    public List<Issue> listByStatus(Status status) {
        return store.values().stream()
                .filter(issue -> issue.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Issue> listAll() {
        return store.values().stream().toList();
    }
}

