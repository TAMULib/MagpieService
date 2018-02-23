package edu.tamu.app.config;

import java.io.IOException;
import java.util.List;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.weaver.auth.resolver.WeaverCredentialsArgumentResolver;
import edu.tamu.weaver.auth.resolver.WeaverUserArgumentResolver;
import edu.tamu.weaver.validation.resolver.WeaverValidatedModelMethodProcessor;

@EnableWebMvc
@Configuration
@EntityScan(basePackages = { "edu.tamu.app.model", "edu.tamu.weaver.wro.model" })
@EnableJpaRepositories(basePackages = { "edu.tamu.app.model.repo", "edu.tamu.weaver.wro.model.repo" })
public class AppWebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("${app.security.allow-access}")
    private String[] hosts;

    @Value("${app.assets.path}")
    private String assetsPath;

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Bean
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/admin/h2console/*");
        registrationBean.addInitParameter("-webAllowOthers", "true");
        return registrationBean;
    }

    @Bean
    public ConfigurableMimeFileTypeMap configurableMimeFileTypeMap() {
        return new ConfigurableMimeFileTypeMap();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // @formatter:off
        registry.addMapping("/**")
                .allowedOrigins(hosts)
                .allowCredentials(false)
                .allowedMethods("GET", "DELETE", "PUT", "POST")
                .allowedHeaders("Origin", "Content-Type", "Access-Control-Allow-Origin", "x-requested-with", "jwt", "data", "x-forwarded-for")
                .exposedHeaders("jwt");
        // @formatter:on
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String assetsFullPath;
        try {
            assetsFullPath = resourceLoader.getResource(assetsPath).getURI().getPath();
        } catch (IOException e) {
            assetsFullPath = assetsPath;
        }
        registry.addResourceHandler("/**").addResourceLocations("file:" + assetsFullPath + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WeaverValidatedModelMethodProcessor(converters));
        argumentResolvers.add(new WeaverCredentialsArgumentResolver());
        argumentResolvers.add(new WeaverUserArgumentResolver<AppUser, AppUserRepo>(appUserRepo));
    }

}