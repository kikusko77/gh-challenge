package com.facade.mapper;

import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.UpdateStatusRequestDTO;
import com.domain.model.Issue;
import com.domain.model.Status;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IssueMapper {
    IssueViewDTO toIssueViewDTO(Issue issue);
    Issue toIssue(UpdateStatusRequestDTO updateStatusRequestDTO);
    Status toStatus(com.example.contracts.dto.Status status);
    List<IssueViewDTO> toIssueViewDTOList(List<Issue> issues);

    default OffsetDateTime map(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    default Instant map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }
}
