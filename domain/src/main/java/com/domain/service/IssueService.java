package com.domain.service;

import com.domain.model.Issue;
import com.domain.model.Status;

import java.util.List;

public interface IssueService {
    Issue create(String description, String parentId);
    Issue updateStatus(String id, Status newStatus);
    List<Issue> listByStatus(Status status);
}
