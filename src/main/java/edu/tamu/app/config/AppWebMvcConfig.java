package edu.tamu.app.config;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.AppUser;
import edu.tamu.weaver.auth.resolver.WeaverCredentialsArgumentResolver;
import edu.tamu.weaver.auth.resolver.WeaverUserArgumentResolver;
import edu.tamu.weaver.validation.resolver.WeaverValidatedModelMethodProcessor;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class AppWebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;
    
    @Autowired
    private List<HttpMessageConverter<?>> converters;
    
    @Autowired
    private AppUserRepo appUserRepo;

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }
    
    @Bean
    public ConfigurableMimeFileTypeMap configurableMimeFileTypeMap() {
        return new ConfigurableMimeFileTypeMap();
    }
    
    /**
     * Executor Service configuration.
     * 
     * @return ExecutorSevice
     * 
     */
    @Bean(name = "executorService")
    private static ExecutorService configureExecutorService() {
        ExecutorService executorService = new ThreadPoolExecutor(10, 25, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(25));
        return executorService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        boolean devMode = this.env.acceptsProfiles("dev");
        boolean useResourceCache = !devMode;
        Integer cachePeriod = devMode ? 0 : null;

        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").setCachePeriod(cachePeriod).resourceChain(useResourceCache).addResolver(new VersionResourceResolver().addContentVersionStrategy("/**")).addTransformer(new AppCacheManifestTransformer());
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WeaverValidatedModelMethodProcessor(converters));
        argumentResolvers.add(new WeaverCredentialsArgumentResolver());
        argumentResolvers.add(new WeaverUserArgumentResolver<AppUser, AppUserRepo>(appUserRepo));
    }

}