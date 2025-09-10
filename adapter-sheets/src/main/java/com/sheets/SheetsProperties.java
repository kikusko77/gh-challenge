package com.sheets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sheets")
public class SheetsProperties {

    private String spreadsheetId;
    private String sheetName = "issues";
    private String dataRange = "issues!A2:F";
    private String credentialsPath;
}
