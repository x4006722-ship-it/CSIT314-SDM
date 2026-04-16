package com.uow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final @NonNull UserAdminAuthInterceptor userAdminAuthInterceptor;

    public WebConfig(@NonNull UserAdminAuthInterceptor userAdminAuthInterceptor) {
        this.userAdminAuthInterceptor = userAdminAuthInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(userAdminAuthInterceptor)
                .addPathPatterns(
                        "/ManageAccount.html",
                        "/ManageProfile.html",
                        "/api/accounts/**",
                        "/api/profiles/**");
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/LoginPage.html");
    }
}