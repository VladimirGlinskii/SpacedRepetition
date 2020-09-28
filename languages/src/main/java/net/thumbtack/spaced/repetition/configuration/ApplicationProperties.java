package net.thumbtack.spaced.repetition.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    @Value("${app.jwt.token.secret}")
    private String jwtTokenSecret;

    @Value("${app.jwt.token.expired}")
    private long jwtTokenExpired;

    @Value("${app.fibonacci.sequence.length}")
    private int fibonacciLength;

    @Value("${app.max.page.size}")
    private int maxPageSize;

    @Value("${app.minPasswordLength}")
    private int minPasswordLength;

    @Value("${app.maxPasswordLength}")
    private int maxPasswordLength;

    @Value("${app.maxNameLength}")
    private int maxNameLength;

    public ApplicationProperties() {
    }

    public ApplicationProperties(String jwtTokenSecret, long jwtTokenExpired,
                                 int fibonacciLength,
                                 int maxPageSize, int minPasswordLength,
                                 int maxPasswordLength, int maxNameLength) {
        this.jwtTokenSecret = jwtTokenSecret;
        this.jwtTokenExpired = jwtTokenExpired;
        this.fibonacciLength = fibonacciLength;
        this.maxPageSize = maxPageSize;
        this.minPasswordLength = minPasswordLength;
        this.maxPasswordLength = maxPasswordLength;
        this.maxNameLength = maxNameLength;
    }

    public String getJwtTokenSecret() {
        return jwtTokenSecret;
    }

    public long getJwtTokenExpired() {
        return jwtTokenExpired;
    }

    public int getFibonacciLength() {
        return fibonacciLength;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }
}
