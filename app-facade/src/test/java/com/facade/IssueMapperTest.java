package com.facade;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.UpdateStatusRequestDTO;
import com.facade.mapper.IssueMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueMapperTest {

    IssueMapper mapper = Mappers.getMapper(IssueMapper.class);

    @Test
    void maps_issue_to_issueViewDTO_with_timestamps() {
        Instant createdAt = Instant.parse("2025-09-10T10:00:00Z");
        Instant updatedAt = Instant.parse("2025-09-10T11:00:00Z");

        Issue issue = new Issue("AD-1", "Bug", null, Status.OPEN, createdAt, updatedAt);

        IssueViewDTO dto = mapper.toIssueViewDTO(issue);

        assertThat(dto.getId()).isEqualTo("AD-1");
        assertThat(dto.getDescription()).isEqualTo("Bug");
        assertThat(dto.getStatus().name()).isEqualTo("OPEN");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt.atOffset(ZoneOffset.UTC));
        assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt.atOffset(ZoneOffset.UTC));
    }

    @Test
    void maps_updateStatusRequestDTO_to_issue() {
        UpdateStatusRequestDTO dto = new UpdateStatusRequestDTO();
        dto.setId("AD-2");
        dto.setStatus(com.example.contracts.dto.Status.CLOSED);

        Issue issue = mapper.toIssue(dto);

        assertThat(issue.getId()).isEqualTo("AD-2");
        assertThat(issue.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    void maps_status_enum_correctly() {
        assertThat(mapper.toStatus(com.example.contracts.dto.Status.IN_PROGRESS))
                .isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void converts_instant_and_offsetDateTime() {
        Instant instant = Instant.parse("2025-09-10T12:00:00Z");
        OffsetDateTime odt = mapper.map(instant);

        assertThat(odt).isEqualTo(instant.atOffset(ZoneOffset.UTC));
        assertThat(mapper.map(odt)).isEqualTo(instant);

        assertThat(mapper.map((Instant) null)).isNull();
        assertThat(mapper.map((OffsetDateTime) null)).isNull();
    }

    @Test
    void maps_list_of_issues() {
        Issue issue = new Issue("AD-3", "Test", null, Status.OPEN,
                Instant.parse("2025-09-10T13:00:00Z"), null);

        List<IssueViewDTO> list = mapper.toIssueViewDTOList(List.of(issue));

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo("AD-3");
    }
}
