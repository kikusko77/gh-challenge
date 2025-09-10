package com.sheets;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsIssueRepositoryTest {

    private static final String SPREADSHEET_ID = "sheet-123";
    private static final String SHEET_NAME     = "issues";
    private static final String DATA_RANGE     = "issues!A2:F";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Sheets sheets;

    GoogleSheetsIssueRepository repo;

    @BeforeEach
    void setUp() {
        repo = new GoogleSheetsIssueRepository(sheets, SPREADSHEET_ID, SHEET_NAME, DATA_RANGE);
    }

    private ValueRange vr(List<List<Object>> rows) {
        return new ValueRange().setValues(rows);
    }

    @Test
    void listAll_returns_rows_mapped() throws Exception {
        when(sheets.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute())
                .thenReturn(vr(List.of(
                        List.of("AD-1", "Login bug", "", "OPEN", "2024-10-01T10:02:00Z", ""),
                        List.of("AD-2", "Export crashes", "AD-1", "CLOSED", "2024-10-02T12:00:00Z", "2024-10-03T09:00:00Z")
                )));

        var all = repo.listAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(Issue::getId).containsExactly("AD-1", "AD-2");
        assertThat(all.get(0).getStatus()).isEqualTo(Status.OPEN);
        assertThat(all.get(1).getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    void listAll_handles_null_values_as_empty_list() throws Exception {
        when(sheets.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute())
                .thenReturn(new ValueRange().setValues(null));

        assertThat(repo.listAll()).isEmpty();
    }

    @Test
    void listByStatus_filters_correctly() throws Exception {
        when(sheets.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute())
                .thenReturn(vr(List.of(
                        List.of("AD-1", "Login bug", "", "OPEN", "2024-10-01T10:02:00Z", ""),
                        List.of("AD-2", "Export crashes", "AD-1", "CLOSED", "2024-10-02T12:00:00Z", "")
                )));

        var open = repo.listByStatus(Status.OPEN);

        assertThat(open).hasSize(1);
        assertThat(open.getFirst().getId()).isEqualTo("AD-1");
    }

    @Test
    void findById_returns_match() throws Exception {
        when(sheets.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute())
                .thenReturn(vr(List.of(
                        List.of("AD-1", "Login bug"),
                        List.of("AD-42", "Target issue")
                )));

        Optional<Issue> found = repo.findById("AD-42");

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Target issue");
    }

    @Test
    void create_appends_row() throws Exception {
        // Arrange: mock the chain pieces explicitly
        Sheets.Spreadsheets spreadsheets = mock(Sheets.Spreadsheets.class);
        Sheets.Spreadsheets.Values values = mock(Sheets.Spreadsheets.Values.class);
        Sheets.Spreadsheets.Values.Append appendReq = mock(Sheets.Spreadsheets.Values.Append.class);

        when(sheets.spreadsheets()).thenReturn(spreadsheets);
        when(spreadsheets.values()).thenReturn(values);
        // capture the ValueRange body passed to append
        ArgumentCaptor<ValueRange> bodyCap = ArgumentCaptor.forClass(ValueRange.class);
        when(values.append(eq(SPREADSHEET_ID), eq(SHEET_NAME), bodyCap.capture()))
                .thenReturn(appendReq);
        when(appendReq.setValueInputOption("RAW")).thenReturn(appendReq);
        when(appendReq.execute()).thenReturn(new AppendValuesResponse());

        Issue i = new Issue("AD-5", "New bug", null, Status.OPEN,
                Instant.parse("2025-09-10T16:00:00Z"), null);

        // Act
        Issue created = repo.create(i);

        // Assert: body content
        assertThat(created.getId()).isEqualTo("AD-5");
        List<Object> row = bodyCap.getValue().getValues().get(0);
        assertThat(row).containsExactly("AD-5", "New bug", "", "OPEN", "2025-09-10T16:00:00Z", "");

        // Assert: fluent calls on the same request mock
        verify(values).append(eq(SPREADSHEET_ID), eq(SHEET_NAME), any(ValueRange.class));
        verify(appendReq).setValueInputOption("RAW");
        verify(appendReq).execute();
        verifyNoMoreInteractions(appendReq);
    }


    @Test
    void updateStatus_updates_correct_row_and_range() throws Exception {
        // Data starts at A2 (header skipped); AD-2 is second data row => sheet row index should be 3
        when(sheets.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute())
                .thenReturn(vr(List.of(
                        List.of("AD-1", "Login bug", "", "OPEN", "2024-10-01T10:02:00Z", ""),
                        List.of("AD-2", "Export crashes", "AD-1", "OPEN", "2024-10-02T12:00:00Z", "")
                )));

        when(sheets.spreadsheets().values()
                .update(eq(SPREADSHEET_ID), anyString(), any(ValueRange.class))
                .setValueInputOption("RAW")
                .execute())
                .thenReturn(new UpdateValuesResponse());

        Instant now = Instant.parse("2025-09-10T17:00:00Z");
        Issue updated = repo.updateStatus("AD-2", Status.CLOSED, now);

        assertThat(updated.getStatus()).isEqualTo(Status.CLOSED);
        assertThat(updated.getUpdatedAt()).isEqualTo(now);

        // Verify correct range and payload
        ArgumentCaptor<String> rangeCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ValueRange> bodyCap = ArgumentCaptor.forClass(ValueRange.class);

        verify(sheets.spreadsheets().values())
                .update(eq(SPREADSHEET_ID), rangeCap.capture(), bodyCap.capture());
        assertThat(rangeCap.getValue()).isEqualTo("issues!A3");

        List<Object> row = bodyCap.getValue().getValues().getFirst();
        assertThat(row.get(0)).isEqualTo("AD-2");
        assertThat(row.get(3)).isEqualTo("CLOSED"); // status updated
        assertThat(row.get(5)).isEqualTo("2025-09-10T17:00:00Z"); // updatedAt
    }

    @Test
    void updateStatus_throws_when_issue_not_found() throws Exception {
        when(sheets.spreadsheets().values().get(SPREADSHEET_ID, DATA_RANGE).execute())
                .thenReturn(vr(List.of(List.of("AD-1", "x"))));

        assertThatThrownBy(() ->
                repo.updateStatus("NOPE", Status.OPEN, Instant.now())
        ).isInstanceOf(NoSuchElementException.class);
    }
}
