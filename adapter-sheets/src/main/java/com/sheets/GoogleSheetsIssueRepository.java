package com.sheets;

import com.domain.model.Issue;
import com.domain.model.Status;
import com.domain.ports.IssueRepository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class GoogleSheetsIssueRepository implements IssueRepository {

    private final Sheets sheets;
    private final String spreadsheetId;
    private final String sheetName;
    private final String dataRange;
    private final SheetsRowMapper rowMapper;

    public GoogleSheetsIssueRepository(Sheets sheets, String spreadsheetId, String sheetName, String dataRange) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
        this.dataRange = dataRange;
        this.rowMapper = new SheetsRowMapper();
    }

    @Override
    public Issue create(Issue issue) {
        List<Object> row = rowMapper.toRow(issue);
        ValueRange appendBody = new ValueRange().setValues(List.of(row));

        try {
            sheets.spreadsheets().values()
                    .append(spreadsheetId, sheetName, appendBody)
                    .setValueInputOption("RAW")
                    .execute();
            return issue;
        } catch (IOException e) {
            throw new RuntimeException("Failed to append issue to Google Sheet", e);
        }
    }

    @Override
    public Optional<Issue> findById(String id) {
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, dataRange)
                    .execute();

            if (response.getValues() == null) {
                return Optional.empty();
            }

            return response.getValues().stream()
                    .map(rowMapper::fromRow)
                    .filter(issue -> issue.getId().equals(id))
                    .findFirst();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read issues from Google Sheet", e);
        }
    }

    @Override
    public Issue updateStatus(String id, Status newStatus, Instant updatedAt) {
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, dataRange)
                    .execute();

            if (response.getValues() == null) {
                throw new NoSuchElementException("Issue not found: " + id);
            }

            List<List<Object>> rows = response.getValues();
            for (int i = 0; i < rows.size(); i++) {
                Issue issue = rowMapper.fromRow(rows.get(i));
                if (issue.getId().equals(id)) {
                    issue.setStatus(newStatus);
                    issue.setUpdatedAt(updatedAt);

                    List<Object> updatedRow = rowMapper.toRow(issue);
                    int sheetRowIndex = i + 2;
                    String range = sheetName + "!A" + sheetRowIndex;
                    ValueRange body = new ValueRange().setValues(List.of(updatedRow));
                    sheets.spreadsheets().values()
                            .update(spreadsheetId, range, body)
                            .setValueInputOption("RAW")
                            .execute();

                    return issue;
                }
            }

            throw new NoSuchElementException("Issue not found: " + id);

        } catch (IOException e) {
            throw new RuntimeException("Failed to update issue in Google Sheet", e);
        }
    }

    @Override
    public List<Issue> listByStatus(Status status) {
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, dataRange)
                    .execute();

            if (response.getValues() == null) {
                return List.of();
            }

            return response.getValues().stream()
                    .map(rowMapper::fromRow)
                    .filter(issue -> issue.getStatus() == status)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Failed to list issues from Google Sheet", e);
        }
    }

    @Override
    public List<Issue> listAll() {
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, dataRange)
                    .execute();

            if (response.getValues() == null) {
                return List.of();
            }

            return response.getValues().stream()
                    .map(rowMapper::fromRow)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Failed to read all issues from Google Sheet", e);
        }
    }

}
