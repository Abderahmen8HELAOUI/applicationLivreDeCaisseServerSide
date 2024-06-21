package com.example.demo.config;

import com.example.demo.auditing.ApplicationAuditAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class ApplicationConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditAware();
    }
}
