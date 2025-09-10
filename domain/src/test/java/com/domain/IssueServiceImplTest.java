package com.domain;

import com.domain.error.IssueNotFoundException;
import com.domain.error.ValidationException;
import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.ports.Clock;
import com.domain.ports.IdGenerator;
import com.domain.ports.IssueRepository;
import com.domain.service.IssueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class IssueServiceImplTest {

    private IssueRepository repo;
    private IdGenerator idGen;
    private Clock clock;
    private IssueServiceImpl service;

    private final Instant FIXED_NOW = Instant.parse("2025-09-10T10:00:00Z");

    @BeforeEach
    void setUp() {
        repo = mock(IssueRepository.class);
        idGen = mock(IdGenerator.class);
        clock = mock(Clock.class);

        service = new IssueServiceImpl(repo, idGen, clock);

        when(clock.now()).thenReturn(FIXED_NOW);
    }

    @Test
    void create_valid_issue_saves_and_returns_it() {
        when(idGen.nextId()).thenReturn("AD-42");

        Issue created = new Issue("AD-42", "Bug", null, Status.OPEN, FIXED_NOW, null);
        when(repo.create(any())).thenReturn(created);

        Issue result = service.create("Bug", null);

        assertThat(result.getId()).isEqualTo("AD-42");
        assertThat(result.getDescription()).isEqualTo("Bug");
        assertThat(result.getStatus()).isEqualTo(Status.OPEN);
        assertThat(result.getCreatedAt()).isEqualTo(FIXED_NOW);
        assertThat(result.getUpdatedAt()).isNull();

        verify(repo).create(argThat(i ->
                i.getId().equals("AD-42") &&
                        i.getDescription().equals("Bug") &&
                        i.getStatus() == Status.OPEN &&
                        i.getCreatedAt().equals(FIXED_NOW) &&
                        i.getUpdatedAt() == null
        ));
    }

    @Test
    void create_blank_description_throws() {
        assertThatThrownBy(() -> service.create("   ", null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Description must not be blank");
    }

    @Test
    void create_with_nonexistent_parent_throws() {
        when(repo.findById("NOPE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("Bug", "NOPE"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Parent issue does not exist: NOPE");
    }

    @Test
    void updateStatus_existing_issue_updates_repo() {
        Issue existing = new Issue("AD-1", "Bug", null, Status.OPEN, FIXED_NOW, null);
        when(repo.findById("AD-1")).thenReturn(Optional.of(existing));

        Issue updated = new Issue("AD-1", "Bug", null, Status.CLOSED, FIXED_NOW, FIXED_NOW);
        when(repo.updateStatus("AD-1", Status.CLOSED, FIXED_NOW)).thenReturn(updated);

        Issue result = service.updateStatus("AD-1", Status.CLOSED);

        assertThat(result.getStatus()).isEqualTo(Status.CLOSED);
        assertThat(result.getUpdatedAt()).isEqualTo(FIXED_NOW);

        verify(repo).findById("AD-1");
        verify(repo).updateStatus("AD-1", Status.CLOSED, FIXED_NOW);
    }

    @Test
    void updateStatus_nonexistent_issue_throws() {
        when(repo.findById("AD-99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus("AD-99", Status.OPEN))
                .isInstanceOf(IssueNotFoundException.class)
                .hasMessageContaining("AD-99");
    }

    @Test
    void listByStatus_returns_issues_from_repo() {
        Issue i1 = new Issue("AD-1", "Bug", null, Status.OPEN, FIXED_NOW, null);
        Issue i2 = new Issue("AD-2", "Crash", null, Status.OPEN, FIXED_NOW, null);

        when(repo.listByStatus(Status.OPEN)).thenReturn(List.of(i1, i2));

        List<Issue> result = service.listByStatus(Status.OPEN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("AD-1");
        assertThat(result.get(1).getId()).isEqualTo("AD-2");

        verify(repo).listByStatus(Status.OPEN);
    }
}
