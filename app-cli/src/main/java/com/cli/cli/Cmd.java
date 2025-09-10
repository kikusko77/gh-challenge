package com.cli.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
        name = "issues-cli",
        description = "Issue tracking CLI",
        subcommands = {
                CreateCmd.class,
                ListCmd.class,
                UpdateCmd.class
        },
        mixinStandardHelpOptions = true
)
public class Cmd implements Runnable {
    @Override
    public void run() {
        System.out.println("Use one of the subcommands: create, list, update");
    }
}
