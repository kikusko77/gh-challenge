package com.cli.cli;

import com.example.contracts.dto.CreateIssueRequestDTO;
import com.example.contracts.dto.IssueViewDTO;
import com.facade.IssueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
        name = "create",
        description = "Create a new issue"
)
@RequiredArgsConstructor
public class CreateCmd implements Runnable {

    private final IssueFacade facade;

    @CommandLine.Option(
            names = {"-d", "--description"},
            required = true,
            description = "Issue description"
    )
    private String description;

    @CommandLine.Option(
            names = {"-p", "--parent"},
            description = "Optional parent issue ID"
    )
    private String parentId;

    @Override
    public void run() {
        CreateIssueRequestDTO req = new CreateIssueRequestDTO();
        req.setDescription(description);
        req.setParentId(parentId);

        IssueViewDTO created = facade.createIssue(req);
        System.out.printf("Created issue %s: %s%n", created.getId(), created.getDescription());
    }
}
