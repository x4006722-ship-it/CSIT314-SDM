package com.uow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserAdminAuthInterceptor userAdminAuthInterceptor;

    public WebConfig(UserAdminAuthInterceptor userAdminAuthInterceptor) {
        this.userAdminAuthInterceptor = userAdminAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAdminAuthInterceptor)
                .addPathPatterns(
                        "/ManageAccount.html",
                        "/ManageProfile.html",
                        "/api/accounts/**",
                        "/api/profiles/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/LoginPage.html");
    }
}