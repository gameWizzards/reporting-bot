package com.telegram.reporting.config;

import com.telegram.reporting.service.I18nPropsResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@EnableAsync
@Configuration
public class AppConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        Locale.setDefault(Locale.forLanguageTag(I18nPropsResolver.DEFAULT_LOCALE.toLanguageTag()));
        var source = new ResourceBundleMessageSource();
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setBasenames("messages/bot-messages", "messages/buttons");
        return source;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
