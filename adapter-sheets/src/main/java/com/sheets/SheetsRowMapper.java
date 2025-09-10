package com.sheets;

import com.domain.model.Issue;
import com.domain.model.Status;

import java.time.Instant;
import java.util.List;

public class SheetsRowMapper {

    public List<Object> toRow(Issue issue) {
        return List.of(
                issue.getId(),
                issue.getDescription(),
                issue.getParentId() != null ? issue.getParentId() : "",
                issue.getStatus() != null ? issue.getStatus().name() : "",
                issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : "",
                issue.getUpdatedAt() != null ? issue.getUpdatedAt().toString() : ""
        );
    }

    public Issue fromRow(List<Object> row) {
        String id = row.get(0).toString();
        String description = row.get(1).toString();

        String parentId = row.size() > 2 && !row.get(2).toString().isBlank()
                ? row.get(2).toString()
                : null;

        Status status = row.size() > 3 && !row.get(3).toString().isBlank()
                ? Status.valueOf(row.get(3).toString().trim().toUpperCase())
                : null;

        Instant createdAt = row.size() > 4 && !row.get(4).toString().isBlank()
                ? Instant.parse(row.get(4).toString())
                : null;

        Instant updatedAt = row.size() > 5 && !row.get(5).toString().isBlank()
                ? Instant.parse(row.get(5).toString())
                : null;

        return new Issue(id, description, parentId, status, createdAt, updatedAt);
    }
}
