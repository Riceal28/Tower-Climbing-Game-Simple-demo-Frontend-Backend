package com.szm.demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<GameContextFilter> gameContextFilter(){
        FilterRegistrationBean<GameContextFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new GameContextFilter());
        bean.addUrlPatterns("/api/*");
        bean.setOrder(1);

        return bean;
    }
}
