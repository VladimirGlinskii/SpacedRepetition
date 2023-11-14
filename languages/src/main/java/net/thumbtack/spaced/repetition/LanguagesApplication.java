package net.thumbtack.spaced.repetition;

import net.thumbtack.spaced.repetition.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import({ApplicationProperties.class})
@EnableScheduling
public class LanguagesApplication {
    public static void main(String[] args) {
        SpringApplication.run(LanguagesApplication.class, args);
    }
}
