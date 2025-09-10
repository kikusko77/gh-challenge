package com.domain.service;

import com.domain.error.IssueNotFoundException;
import com.domain.error.ValidationException;
import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.ports.Clock;
import com.domain.ports.IdGenerator;
import com.domain.ports.IssueRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
public class IssueServiceImpl implements IssueService {

    private IssueRepository issueRepository;
    private final IdGenerator idGenerator;
    private final Clock clock;

    @Override
    public Issue create(String description, String parentId) {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Description must not be blank");
        }
        if (parentId != null && issueRepository.findById(parentId).isEmpty()) {
            throw new ValidationException("Parent issue does not exist: " + parentId);
        }
        String id = idGenerator.nextId();
        Instant now = clock.now();
        Issue issue = new Issue(id, description, parentId, Status.OPEN, now, null);
        return issueRepository.create(issue);
    }

    @Override
    public Issue updateStatus(String id, Status newStatus) {
        Issue existing = issueRepository.findById(id)
                .orElseThrow(() -> new IssueNotFoundException(id));

        Instant now = clock.now();
        return issueRepository.updateStatus(id, newStatus, now);
    }

    @Override
    public List<Issue> listByStatus(Status status) {
        return issueRepository.listByStatus(status);
    }
}
