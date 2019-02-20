package com.seerlogics.botadmin;

import com.seerlogics.botadmin.exception.GlobalExceptionHandler;
import com.seerlogics.cloud.ManageDataStore;
import com.seerlogics.cloud.ManageInstance;
import com.seerlogics.cloud.ManageLoadBalancer;
import com.seerlogics.cloud.aws.ec2.ManageInstanceImpl;
import com.seerlogics.cloud.aws.elb.ManageLoadBalancerImpl;
import com.seerlogics.cloud.aws.s3.ManageDataStoreImpl;
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
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
     * This ALLOWS ALL request to go through.
     * todo make this more restrictive per the domain desired.
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
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

    @Bean(name = "botAdminSystemProperties")
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        /**
         * to make this work pass the following VM args to tomcat:
         * -DdepProfile=dev -DconfigLoc=/opt/installs/tomcat/8.5.9
         */
        Resource[] validatorResources = new Resource[]{
                resourceLoader.getResource("file:" + System.getProperty("configLoc")
                        + "/config/botadmin/config_base.properties"),
                resourceLoader.getResource("file:" + System.getProperty("configLoc")
                        + "/config/botadmin/config_" + System.getProperty("depProfile") + ".properties")
        };
        propertySourcesPlaceholderConfigurer.setLocations(validatorResources);
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public ManageInstance deployToCloud() {
        return new ManageInstanceImpl();
    }

    @Bean
    public ManageDataStore deployToDataStore() {
        return new ManageDataStoreImpl();
    }

    @Bean
    public ManageLoadBalancer manageLoadBalancer() {
        return new ManageLoadBalancerImpl();
    }

    /*@Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("*//**", new CorsConfiguration().applyPermitDefaultValues());
     return source;
     }*/
}