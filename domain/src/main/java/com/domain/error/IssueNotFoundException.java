package com.domain.error;

public class IssueNotFoundException extends RuntimeException {
    private final String issueId;

    public IssueNotFoundException(String issueId) {
        super("Issue not found: " + issueId);
        this.issueId = issueId;
    }

    public String getIssueId() {
        return issueId;
    }
}
