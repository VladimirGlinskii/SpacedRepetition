package net.thumbtack.spaced.repetition.configuration;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfiguration {

    @Bean
    public DelegatingPasswordEncoder delegatingPasswordEncoder() {
        PasswordEncoder defaultEncoder = new LdapShaPasswordEncoder();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("ldap", defaultEncoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(
                "ldap", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(defaultEncoder);

        return passwordEncoder;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("128KB"));
        factory.setMaxRequestSize(DataSize.parse("128KB"));
        return factory.createMultipartConfig();
    }

}
