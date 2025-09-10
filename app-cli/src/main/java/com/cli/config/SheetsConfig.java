package com.cli.config;

import com.domain.ports.IssueRepository;
import com.sheets.GoogleSheetsIssueRepository;
import com.sheets.SheetsClientFactory;
import com.sheets.SheetsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("sheets")
@EnableConfigurationProperties(SheetsProperties.class)
public class SheetsConfig {

    @Bean
    public IssueRepository issueRepository(SheetsProperties props) {
        return new GoogleSheetsIssueRepository(
                SheetsClientFactory.create(props.getCredentialsPath(), "issues-cli"),
                props.getSpreadsheetId(),
                props.getSheetName(),
                props.getDataRange()
        );
    }
}

