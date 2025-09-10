package com.cli.config;

import com.facade.IssueFacade;
import com.local.SequentialIdGenerator;
import com.local.SystemClock;
import com.facade.mapper.IssueMapper;
import com.domain.ports.Clock;
import com.domain.ports.IdGenerator;
import com.domain.ports.IssueRepository;
import com.domain.service.IssueService;
import com.domain.service.IssueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig { ;

    @Bean
    public IdGenerator idGenerator(IssueRepository issueRepository) {
        return new SequentialIdGenerator("AD-", issueRepository );
    }

    @Bean
    public Clock clock() {
        return new SystemClock();
    }

    @Bean
    public IssueService issueService(IssueRepository repo, IdGenerator ids, Clock clock) {
        return new IssueServiceImpl(repo, ids, clock);
    }

    @Bean
    public IssueFacade issueFacade(IssueService service, IssueMapper mapper) {
        return new IssueFacade(service, mapper);
    }
}
