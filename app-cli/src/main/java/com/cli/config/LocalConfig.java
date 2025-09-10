package com.cli.config;

import com.domain.ports.IssueRepository;
import com.local.InMemoryIssueRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalConfig {
    @Bean
    public IssueRepository issueRepository() {
        return new InMemoryIssueRepository();
    }
}
