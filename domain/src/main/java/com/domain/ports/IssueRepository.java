package com.domain.ports;

import com.domain.model.Issue;
import com.domain.model.Status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    Issue create(Issue issue);

    Optional<Issue> findById(String id);

    Issue updateStatus(String id, Status newStatus, Instant updatedAt);

    List<Issue> listByStatus(Status status);

    List<Issue> listAll();
}
