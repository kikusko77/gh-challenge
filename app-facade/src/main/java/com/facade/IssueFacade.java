package com.facade;

import com.example.contracts.dto.CreateIssueRequestDTO;
import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.UpdateStatusRequestDTO;
import com.facade.mapper.IssueMapper;
import com.domain.model.Issue;
import com.example.contracts.dto.Status;
import com.domain.service.IssueService;

import java.util.List;

public class IssueFacade {

    private final IssueService issueService;
    private final IssueMapper issueMapper;
    public IssueFacade(IssueService issueService, IssueMapper issueMapper) {
        this.issueService = issueService;
        this.issueMapper = issueMapper;
    }

    public IssueViewDTO createIssue(CreateIssueRequestDTO createIssueRequestDTO) {
        Issue createIssue = issueService.create(createIssueRequestDTO.getDescription(), createIssueRequestDTO.getParentId());
        return issueMapper.toIssueViewDTO(createIssue);
    }

    public IssueViewDTO updateIssue(UpdateStatusRequestDTO updateStatusRequestDTO) {
        Issue issue = issueMapper.toIssue(updateStatusRequestDTO);
        Issue updateIssue = issueService.updateStatus(issue.getId(), issue.getStatus());
        return issueMapper.toIssueViewDTO(updateIssue);
    }

    public List<IssueViewDTO> listByStatus(Status status) {
        List<Issue> issues = issueService.listByStatus(issueMapper.toStatus(status));
        return issueMapper.toIssueViewDTOList(issues);
    }
}
