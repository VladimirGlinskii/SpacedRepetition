package net.thumbtack.spaced.repetition.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private String jwtTokenSecret;
    private long jwtTokenExpired;
    private int fibonacciSequenceLength;
    private int maxPageSize;
    private int minPasswordLength;
    private int maxPasswordLength;
    private int maxNameLength;

    public ApplicationProperties() {
    }

    public ApplicationProperties(String jwtTokenSecret,
                                 long jwtTokenExpired,
                                 int fibonacciSequenceLength,
                                 int maxPageSize,
                                 int minPasswordLength,
                                 int maxPasswordLength,
                                 int maxNameLength
    ) {
        this.jwtTokenSecret = jwtTokenSecret;
        this.jwtTokenExpired = jwtTokenExpired;
        this.fibonacciSequenceLength = fibonacciSequenceLength;
        this.maxPageSize = maxPageSize;
        this.minPasswordLength = minPasswordLength;
        this.maxPasswordLength = maxPasswordLength;
        this.maxNameLength = maxNameLength;
    }

}
