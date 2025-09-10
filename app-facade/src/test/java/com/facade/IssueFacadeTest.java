package com.facade;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.service.IssueService;
import com.example.contracts.dto.CreateIssueRequestDTO;
import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.UpdateStatusRequestDTO;
import com.facade.mapper.IssueMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IssueFacadeTest {

    private IssueService service;
    private IssueMapper mapper;
    private IssueFacade facade;

    @BeforeEach
    void setUp() {
        service = mock(IssueService.class);
        mapper = mock(IssueMapper.class);
        facade = new IssueFacade(service, mapper);
    }

    @Test
    void createIssue_delegates_and_returns_dto() {
        CreateIssueRequestDTO req = new CreateIssueRequestDTO();
        req.setDescription("New bug");
        req.setParentId("AD-1");

        Issue domainIssue = new Issue("AD-42", "New bug", "AD-1", Status.OPEN, null, null);
        IssueViewDTO dto = new IssueViewDTO();
        dto.setId("AD-42");
        dto.setDescription("New bug");

        when(service.create("New bug", "AD-1")).thenReturn(domainIssue);
        when(mapper.toIssueViewDTO(domainIssue)).thenReturn(dto);

        IssueViewDTO result = facade.createIssue(req);

        assertThat(result.getId()).isEqualTo("AD-42");
        verify(service).create("New bug", "AD-1");
        verify(mapper).toIssueViewDTO(domainIssue);
    }

    @Test
    void updateIssue_maps_request_and_delegates() {
        UpdateStatusRequestDTO req = new UpdateStatusRequestDTO();
        req.setId("AD-1");
        req.setStatus(com.example.contracts.dto.Status.CLOSED);

        Issue mapped = new Issue("AD-1", "x", null, Status.CLOSED, null, null);
        Issue updated = new Issue("AD-1", "x", null, Status.CLOSED, null, null);
        IssueViewDTO dto = new IssueViewDTO();
        dto.setId("AD-1");
        dto.setStatus(com.example.contracts.dto.Status.CLOSED);

        when(mapper.toIssue(req)).thenReturn(mapped);
        when(service.updateStatus("AD-1", Status.CLOSED)).thenReturn(updated);
        when(mapper.toIssueViewDTO(updated)).thenReturn(dto);

        IssueViewDTO result = facade.updateIssue(req);

        assertThat(result.getStatus()).isEqualTo(com.example.contracts.dto.Status.CLOSED);
        verify(mapper).toIssue(req);
        verify(service).updateStatus("AD-1", Status.CLOSED);
        verify(mapper).toIssueViewDTO(updated);
    }

    @Test
    void listByStatus_delegates_and_maps_list() {
        Issue domain = new Issue("AD-2", "x", null, Status.IN_PROGRESS, null, null);
        IssueViewDTO dto = new IssueViewDTO();
        dto.setId("AD-2");

        when(mapper.toStatus(com.example.contracts.dto.Status.IN_PROGRESS)).thenReturn(Status.IN_PROGRESS);
        when(service.listByStatus(Status.IN_PROGRESS)).thenReturn(List.of(domain));
        when(mapper.toIssueViewDTOList(List.of(domain))).thenReturn(List.of(dto));

        List<IssueViewDTO> result = facade.listByStatus(com.example.contracts.dto.Status.IN_PROGRESS);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("AD-2");
        verify(service).listByStatus(Status.IN_PROGRESS);
    }
}
