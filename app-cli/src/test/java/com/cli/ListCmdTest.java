package com.cli;

import com.cli.cli.ListCmd;
import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.Status;
import com.facade.IssueFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListCmdTest {

    @Mock
    private IssueFacade facade;

    @Test
    void prints_table_with_collapsed_whitespace_in_description() {
        ListCmd cmd = new ListCmd(facade);

        IssueViewDTO a = new IssueViewDTO();
        a.setId("AD-1");
        a.setDescription("Line1\nLine2\tTabbed   spaces");
        a.setStatus(Status.OPEN);
        a.setCreatedAt(OffsetDateTime.parse("2024-10-01T10:02:00Z"));
        a.setUpdatedAt(null);

        IssueViewDTO b = new IssueViewDTO();
        b.setId("AD-2");
        b.setDescription("Another issue");
        b.setStatus(Status.OPEN);
        b.setCreatedAt(OffsetDateTime.parse("2024-10-02T09:00:00Z"));
        b.setUpdatedAt(OffsetDateTime.parse("2024-10-03T09:00:00Z"));

        when(facade.listByStatus(Status.OPEN)).thenReturn(List.of(a, b));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            int exit = new CommandLine(cmd).execute("-s", "OPEN");
            assertThat(exit).isZero();

            String out = baos.toString();
            assertThat(out).contains("ID", "Description", "Status", "Created At", "Updated At");
            assertThat(out).contains("Line1 Line2 Tabbed spaces");
            assertThat(out).contains("2024-10-01T10:02");
            assertThat(out).contains("2024-10-03T09:00");
        } finally {
            System.setOut(origOut);
        }
    }

    @Test
    void prints_message_when_no_issues() {
        ListCmd cmd = new ListCmd(facade);

        when(facade.listByStatus(Status.CLOSED)).thenReturn(List.of());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            int exit = new CommandLine(cmd).execute("--status", "CLOSED");
            assertThat(exit).isZero();
            assertThat(baos.toString()).contains("No issues found with status CLOSED");
        } finally {
            System.setOut(origOut);
        }
    }
}
