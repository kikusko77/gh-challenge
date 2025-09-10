package com.local;

import com.domain.model.Issue;
import com.domain.model.Status;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class InMemoryIssueRepositoryTest {

    @Test
    void create_and_findById_work() {
        InMemoryIssueRepository repo = new InMemoryIssueRepository();
        Issue i = new Issue("AD-99", "Desc", null, Status.OPEN, Instant.now(), Instant.now());

        repo.create(i);

        assertThat(repo.findById("AD-99")).isPresent();
    }

    @Test
    void updateStatus_updatesStatusAndUpdatedAt() {
        InMemoryIssueRepository repo = new InMemoryIssueRepository();
        Instant newTime = Instant.parse("2025-09-10T16:00:00Z");

        Issue updated = repo.updateStatus("AD-1", Status.CLOSED, newTime);

        assertThat(updated.getStatus()).isEqualTo(Status.CLOSED);
        assertThat(updated.getUpdatedAt()).isEqualTo(newTime);
    }

    @Test
    void updateStatus_unknownId_throws() {
        InMemoryIssueRepository repo = new InMemoryIssueRepository();

        assertThatThrownBy(() -> repo.updateStatus("NOPE", Status.OPEN, Instant.now()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
