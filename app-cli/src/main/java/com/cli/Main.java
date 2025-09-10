package com.cli;

import com.cli.cli.Cmd;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import picocli.CommandLine;

import java.util.Scanner;

@SpringBootApplication(scanBasePackages = {"com.cli", "com.facade", "com.domain", "com.local", "com.sheets"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner commandLineRunner(ApplicationContext ctx) {

        return args -> {
            CommandLine cmd = new CommandLine(ctx.getBean(Cmd.class), new CommandLine.IFactory() {
                @Override
                public <K> K create(Class<K> cls) {
                    return ctx.getBean(cls);
                }
            });

            Scanner scanner = new Scanner(System.in);
            System.out.println("Interactive issue CLI. Type 'exit' to quit.");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Goodbye 👋");
                    break;
                }
                if (input.isEmpty()) continue;

                String[] parsedArgs = input.split("\\s+");
                try {
                    cmd.execute(parsedArgs);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        };
    }
}
