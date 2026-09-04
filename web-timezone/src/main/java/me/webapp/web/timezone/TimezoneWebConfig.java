package me.webapp.web.timezone;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TimezoneWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/web/timezone").setViewName("forward:/web/timezone/index.html");
        registry.addViewController("/web/timezone/").setViewName("forward:/web/timezone/index.html");
    }
}
