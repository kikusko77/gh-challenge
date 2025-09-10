package com.cli;

import com.cli.cli.Cmd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"local", "test"})
class IssueCliFunctionalTest {

    @Autowired
    private Cmd rootCmd;

    @Autowired
    private ApplicationContext ctx;

    private String runCli(String... args) {
        CommandLine cli = new CommandLine(rootCmd, new CommandLine.IFactory() {
            @Override
            public <K> K create(Class<K> cls) {
                return ctx.getBean(cls);
            }
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            int exitCode = cli.execute(args);
            assertThat(exitCode).isZero();
            return baos.toString();
        } finally {
            System.setOut(origOut);
        }
    }

    @Test
    void create_list_update_flow() {
        String out1 = runCli("create", "-d", "Functional bug");
        assertThat(out1).contains("Created issue AD-");

        String createdId = out1.split(" ")[2].replace(":", "").trim();
        assertThat(createdId).startsWith("AD-");

        String out2 = runCli("list", "-s", "OPEN");
        assertThat(out2).contains(createdId);
        assertThat(out2).contains("Functional bug");
        assertThat(out2).contains("OPEN");

        String out3 = runCli("update", "-i", createdId, "-s", "CLOSED");
        assertThat(out3).contains("Updated issue " + createdId + " → CLOSED");

        String out4 = runCli("list", "-s", "CLOSED");
        assertThat(out4).contains(createdId);
        assertThat(out4).contains("CLOSED");
    }
}
