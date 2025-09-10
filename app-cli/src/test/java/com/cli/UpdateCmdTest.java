package com.cli;

import com.cli.cli.UpdateCmd;
import com.example.contracts.dto.IssueViewDTO;
import com.example.contracts.dto.Status;
import com.example.contracts.dto.UpdateStatusRequestDTO;
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
class UpdateCmdTest {

    @Mock
    private IssueFacade facade;

    @Test
    void updates_status_and_prints_confirmation() {
        UpdateCmd cmd = new UpdateCmd(facade);

        IssueViewDTO updated = new IssueViewDTO();
        updated.setId("AD-1");
        updated.setStatus(Status.CLOSED);
        when(facade.updateIssue(any())).thenReturn(updated);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            int exit = new CommandLine(cmd).execute("-i", "AD-1", "-s", "CLOSED");
            assertThat(exit).isZero();

            ArgumentCaptor<UpdateStatusRequestDTO> cap = ArgumentCaptor.forClass(UpdateStatusRequestDTO.class);
            verify(facade).updateIssue(cap.capture());
            assertThat(cap.getValue().getId()).isEqualTo("AD-1");
            assertThat(cap.getValue().getStatus()).isEqualTo(Status.CLOSED);

            assertThat(baos.toString()).contains("Updated issue AD-1 → CLOSED");
        } finally {
            System.setOut(origOut);
        }
    }
}
