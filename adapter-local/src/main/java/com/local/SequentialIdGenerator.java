package com.local;

import com.domain.model.Issue;
import com.domain.ports.IdGenerator;
import com.domain.ports.IssueRepository;

public class SequentialIdGenerator implements IdGenerator {

    private final String prefix;
    private final IssueRepository repo;

    public SequentialIdGenerator(String prefix, IssueRepository repo) {
        this.prefix = prefix;
        this.repo = repo;
    }

    @Override
    public String nextId() {
        int max = 0;
        for (Issue issue : repo.listAll()) {
            String id = issue.getId();
            if (id.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(id.substring(prefix.length()));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return prefix + (max + 1);
    }

}
