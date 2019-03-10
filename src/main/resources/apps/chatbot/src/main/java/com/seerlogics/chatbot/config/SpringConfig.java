package com.seerlogics.chatbot.config;

import com.seerlogics.chatbot.mutters.EventGenieBot;
import com.seerlogics.chatbot.mutters.EventGenieBotConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

/**
 * Created by bkane on 5/6/18.
 * https://www.thomasvitale.com/spring-data-jpa-hibernate-java-configuration/
 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
 */
@Configuration
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {

    @Bean
    public EventGenieBot setupEventGenieBot() {
        return new EventGenieBot(new EventGenieBotConfiguration());
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("bundle/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheMillis(10);
        return messageSource;
    }

    private ClientHttpRequestFactory createRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(20);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(100000).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public VelocityEngine velocityEngine() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("output.encoding", "UTF-8");
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityEngine(properties);
    }

    /**
     * https://spring.io/blog/2015/06/08/cors-support-in-spring-framework#javaconfig
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = new String[]{"http://localhost:4200", "http://localhost:4320",
                "http://localhost:4300",
                "file://", "http://localhost:8000", "http://localhost:8080",
                "http://eventgenie.lingoace.com.s3-website.us-east-2.amazonaws.com", "http://eventgenie.lingoace.com"};
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                        //.allowedHeaders("Authorization, Content-Type")
                        //.exposedHeaders("Authorization, Content-Type")
                .allowedMethods("PUT", "DELETE", "POST", "GET");
    }

    @Bean
    public RestTemplate createRestTemplate() {
        return new RestTemplate(createRequestFactory());
    }
}
