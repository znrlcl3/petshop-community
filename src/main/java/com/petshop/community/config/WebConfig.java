package com.petshop.community.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    
    @Bean
    public FilterRegistrationBean<ErrorPageFilter> disableErrorPageFilter() {
        FilterRegistrationBean<ErrorPageFilter> filterRegistrationBean = 
            new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ErrorPageFilter());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
}