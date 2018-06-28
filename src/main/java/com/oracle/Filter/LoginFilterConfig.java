package com.oracle.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class LoginFilterConfig {
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(domainFilter());
        registration.addUrlPatterns("*");
        registration.setName("domainFilter");
        registration.setOrder(1);
        return registration;
    }

    public Filter domainFilter() {
        return new DomainFilter();
    }
}
