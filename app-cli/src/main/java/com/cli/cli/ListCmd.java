package com.cli.cli;

import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.Status;
import com.facade.IssueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;

@Component
@CommandLine.Command(
        name = "list",
        description = "List issues by status"
)
@RequiredArgsConstructor
public class ListCmd implements Runnable {

    private final IssueFacade facade;

    @CommandLine.Option(
            names = {"-s", "--status"},
            required = true,
            description = "Status to filter by (OPEN, IN_PROGRESS, CLOSED)"
    )
    private Status status;

    @Override
    public void run() {
        List<IssueViewDTO> issues = facade.listByStatus(status);
        if (issues.isEmpty()) {
            System.out.println("No issues found with status " + status);
            return;
        }

        System.out.printf("%-8s %-50s %-12s %-25s %-25s%n",
                "ID", "Description", "Status", "Created At", "Updated At");
        System.out.println("=".repeat(125));
        for (IssueViewDTO issue : issues) {
            System.out.printf("%-6s %-40s %-10s %-25s %-25s%n",
                    issue.getId(),
                    issue.getDescription().replaceAll("\\s+", " "),
                    issue.getStatus(),
                    issue.getCreatedAt(),
                    issue.getUpdatedAt());
        }
    }
}
