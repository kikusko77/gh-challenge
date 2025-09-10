package com.cli;

import com.cli.cli.CreateCmd;
import com.example.contracts.dto.CreateIssueRequestDTO;
import com.example.contracts.dto.IssueViewDTO;
import com.facade.IssueFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCmdTest {

    @Mock
    private IssueFacade facade;

    @Test
    void creates_issue_and_prints_confirmation() {
        CreateCmd cmd = new CreateCmd(facade);

        IssueViewDTO created = new IssueViewDTO();
        created.setId("AD-42");
        created.setDescription("New bug");
        when(facade.createIssue(any())).thenReturn(created);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(baos));

        try {
            int exit = new CommandLine(cmd).execute("--description", "New bug", "--parent", "AD-1");

            assertThat(exit).isZero();

            ArgumentCaptor<CreateIssueRequestDTO> cap = ArgumentCaptor.forClass(CreateIssueRequestDTO.class);
            verify(facade).createIssue(cap.capture());
            assertThat(cap.getValue().getDescription()).isEqualTo("New bug");
            assertThat(cap.getValue().getParentId()).isEqualTo("AD-1");

            String out = baos.toString();
            assertThat(out).contains("Created issue AD-42: New bug");
        } finally {
            System.setOut(origOut);
        }
    }
}
