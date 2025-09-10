package com.sheets;

import com.domain.model.Issue;
import com.domain.model.Status;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SheetsRowMapperTest {

    SheetsRowMapper mapper = new SheetsRowMapper();

    @Test
    void fromRow_minimal_required_fields() {
        List<Object> row = List.of("AD-1", "Login bug");
        Issue i = mapper.fromRow(row);

        assertThat(i.getId()).isEqualTo("AD-1");
        assertThat(i.getDescription()).isEqualTo("Login bug");
        assertThat(i.getParentId()).isNull();
        assertThat(i.getStatus()).isNull();
        assertThat(i.getCreatedAt()).isNull();
        assertThat(i.getUpdatedAt()).isNull();
    }

    @Test
    void fromRow_all_fields_present_case_insensitive_status() {
        List<Object> row = List.of(
                "AD-2", "Export crashes", "AD-1", "open",
                "2024-10-01T10:02:00Z", "2024-10-02T12:00:00Z"
        );
        Issue i = mapper.fromRow(row);

        assertThat(i.getParentId()).isEqualTo("AD-1");
        assertThat(i.getStatus()).isEqualTo(Status.OPEN);
        assertThat(i.getCreatedAt()).isEqualTo(Instant.parse("2024-10-01T10:02:00Z"));
        assertThat(i.getUpdatedAt()).isEqualTo(Instant.parse("2024-10-02T12:00:00Z"));
    }

    @Test
    void toRow_writes_empty_strings_for_null_optionals() {
        Issue i = new Issue("AD-3", "UI issue", null, null, null, null);
        List<Object> row = mapper.toRow(i);

        assertThat(row).containsExactly("AD-3", "UI issue", "", "", "", "");
    }
}
