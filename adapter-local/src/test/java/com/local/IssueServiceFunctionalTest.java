package com.local;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.service.IssueServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueServiceFunctionalTest {

    @Test
    void endToEnd_create_update_list() {
        var repo = new InMemoryIssueRepository();
        var ids = new SequentialIdGenerator("AD-", repo);
        var clock = new SystemClock();
        var service = new IssueServiceImpl(repo, ids, clock);

        Issue issue = service.create("Functional bug", null);
        assertThat(issue.getId()).isNotBlank();
        assertThat(issue.getStatus()).isEqualTo(Status.OPEN);

        Issue updated = service.updateStatus(issue.getId(), Status.CLOSED);
        assertThat(updated.getStatus()).isEqualTo(Status.CLOSED);

        List<Issue> closedIssues = service.listByStatus(Status.CLOSED);
        assertThat(closedIssues).extracting(Issue::getId).contains(issue.getId());
    }
}
