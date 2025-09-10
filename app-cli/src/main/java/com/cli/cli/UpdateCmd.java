package com.cli.cli;

import com.example.contracts.dto.UpdateStatusRequestDTO;
import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.Status;
import com.facade.IssueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
        name = "update",
        description = "Update the status of an existing issue"
)
@RequiredArgsConstructor
public class UpdateCmd implements Runnable {

    private final IssueFacade facade;

    @CommandLine.Option(
            names = {"-i", "--id"},
            required = true,
            description = "ID of the issue to update"
    )
    private String id;

    @CommandLine.Option(
            names = {"-s", "--status"},
            required = true,
            description = "New status (OPEN, IN_PROGRESS, CLOSED)"
    )
    private Status status;

    @Override
    public void run() {
        UpdateStatusRequestDTO req = new UpdateStatusRequestDTO();
        req.setId(id);
        req.setStatus(status);

        IssueViewDTO updated = facade.updateIssue(req);
        System.out.printf("Updated issue %s → %s%n", updated.getId(), updated.getStatus());
    }
}
