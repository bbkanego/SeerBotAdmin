package com.seerlogics.botadmin.config;

import com.lingoace.cms.CmsClasspathResourceBootstraper;
import com.lingoace.cms.CmsResource;
import com.lingoace.common.ExposedResourceMessageBundleSource;
import com.lingoace.spring.controller.CmsController;
import com.lingoace.spring.controller.ValidationController;
import com.lingoace.spring.interceptor.HandlerInterceptor;
import com.lingoace.validation.ValidationHandler;
import com.lingoace.validation.ValidationHandlerImpl;
import com.lingoace.validation.ValidatorFactory;
import com.lingoace.validation.ValidatorFactoryImpl;
import com.seerlogics.botadmin.factory.ManageDataStoreFactory;
import com.seerlogics.botadmin.factory.ManageInstanceFactory;
import com.seerlogics.botadmin.factory.ManageLoadBalancerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

/**
 * Created by bkane on 10/31/18.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * https://spring.io/blog/2015/06/08/cors-support-in-spring-framework#javaconfig
     * The below will configure ALL the headers and info that the pre-flight call or OPTIONS call will return.
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = new String[]{"http://localhost:4300", "http://localhost:4320",
                "http://eventgenie.lingoace.com.s3-website.us-east-2.amazonaws.com",
                "http://eventgenie.lingoace.com"};
        /**
         * This will be triggered for ALL URLs.
         */
        registry.addMapping("/**")
                /**
                 * This will return allowed origins in "Access-Control-Allow-Origin"
                 */
                .allowedOrigins(allowedOrigins)
                        /**
                         * This will expose header "Access-Control-Allow-Credentials" telling browser that the server
                         * is ready to accept cookies. The UI then will set "withCredentials=true" in JS to send
                         * cookie with request
                         * https://stackoverflow.com/questions/24687313/what-exactly-does-the-access-control-allow-credentials-header-do
                         */
                .allowCredentials(true)
                        /**
                         * This will return allowed methods in "Access-Control-Allow-Methods"
                         */
                .allowedMethods("PUT", "DELETE", "POST", "GET", "OPTIONS");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor());
    }

    @Bean
    public HandlerInterceptor handlerInterceptor() {
        return new HandlerInterceptor("/api");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public CmsResource cmsClasspathResourceBootstraper() throws Exception {
        CmsClasspathResourceBootstraper cmsClasspathResourceBootstraper = new CmsClasspathResourceBootstraper();
        Resource[] cmsResources = loadResources("classpath:cms/*.json");
        cmsClasspathResourceBootstraper.setCmsResources(cmsResources);
        return cmsClasspathResourceBootstraper;
    }

    Resource[] loadResources(String pattern) throws IOException {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
    }

    @Bean
    public ValidatorFactory getValidatorFactory() throws Exception {
        ValidatorFactoryImpl validatorFactory = new ValidatorFactoryImpl();
        Resource[] validatorResources = new Resource[]{
                resourceLoader.getResource("classpath:validator/validator_rules.xml"),
                resourceLoader.getResource("classpath:validator/validator_rules_common.xml")
        };
        validatorFactory.setValidationConfigLocation(validatorResources);
        validatorFactory.afterPropertiesSet();
        return validatorFactory;
    }

    /*@Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }*/

    @Bean
    public ValidationHandler getValidationHandlerImpl() {
        return new ValidationHandlerImpl();
    }

    @Bean(name = "appMessageResource")
    public ExposedResourceMessageBundleSource getExposedResourceMessageBundleSource() {
        ExposedResourceMessageBundleSource exposedResourceMessageBundleSource = new ExposedResourceMessageBundleSource();
        exposedResourceMessageBundleSource.setBasename("classpath:bundle/messages");
        exposedResourceMessageBundleSource.setCacheMillis(1000);
        exposedResourceMessageBundleSource.refreshProperties();
        return exposedResourceMessageBundleSource;
    }

    @Bean
    public ValidationController validationController() {
        return new ValidationController();
    }

    @Bean
    public CmsController cmsController() {
        return new CmsController();
    }

    /*@Bean(name = "botAdminSystemProperties")
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        *//**
     * to make this work pass the following VM args to tomcat:
     * -DdepProfile=dev -DconfigLoc=/opt/installs/tomcat/8.5.9
     *//*
        Resource[] validatorResources = new Resource[]{
                resourceLoader.getResource("file:" + System.getProperty("configLoc")
                        + "/config/botadmin/config_base.properties"),
                resourceLoader.getResource("file:" + System.getProperty("configLoc")
                        + "/config/botadmin/config_" + System.getProperty("depProfile") + ".properties")
        };
        propertySourcesPlaceholderConfigurer.setLocations(validatorResources);
        return propertySourcesPlaceholderConfigurer;
    }*/

    @Bean
    public ManageInstanceFactory deployToCloud() {
        return new ManageInstanceFactory();
    }

    @Bean
    public ManageDataStoreFactory deployToDataStore() {
        return new ManageDataStoreFactory();
    }

    @Bean
    public ManageLoadBalancerFactory manageLoadBalancer() {
        return new ManageLoadBalancerFactory();
    }

    /*@Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("*//**", new CorsConfiguration().applyPermitDefaultValues());
     return source;
     }*/
}