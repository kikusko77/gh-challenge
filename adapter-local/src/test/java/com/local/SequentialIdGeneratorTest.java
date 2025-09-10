package com.local;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.ports.IssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequentialIdGeneratorTest {

    @Mock
    IssueRepository repo;

    @Test
    void nextId_incrementsHighestNumericSuffix() {
        when(repo.listAll()).thenReturn(List.of(
                new Issue("AD-1", "x", null, Status.OPEN, Instant.now(), Instant.now()),
                new Issue("AD-2", "y", null, Status.OPEN, Instant.now(), Instant.now()),
                new Issue("AD-3", "z", null, Status.OPEN, Instant.now(), Instant.now())
        ));

        SequentialIdGenerator gen = new SequentialIdGenerator("AD-", repo);

        String next = gen.nextId();

        assertThat(next).isEqualTo("AD-4");
    }
}
