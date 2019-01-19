package com.lingoace.nlp.config;

import com.lingoace.nlp.mutters.EventGenieBot;
import com.lingoace.nlp.mutters.EventGenieBotConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by bkane on 5/6/18.
 * https://www.thomasvitale.com/spring-data-jpa-hibernate-java-configuration/
 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/EnableWebMvc.html
 */
@Configuration
@EnableWebMvc
@EnableJpaRepositories("com.lingoace.nlp.repository")
@EnableTransactionManagement
@ComponentScan(basePackageClasses = SpringConfig.class,
        basePackages = "com.lingoace.nlp, com.lingoace.common")
// @PropertySource("classpath:jpa.properties")
public class SpringConfig implements WebMvcConfigurer {
    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(getHikariDataSource());
        em.setPackagesToScan("com.lingoace.nlp.model");
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public EventGenieBot setupEventGenieBot() {
        return new EventGenieBot(new EventGenieBotConfiguration());
    }

    // @Bean //todo
    public DataSource getHsqlEmbeddedChatDatabase() {
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder.setType(EmbeddedDatabaseType.HSQL)
                //.addScript("create-db.sql")
                .setName("hsqlMemChatDB")
                .build();
    }

    @Bean
    public HikariDataSource getHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        //--- hikariConfig.setDataSource(getHsqlEmbeddedChatDatabase());
        hikariConfig.setJdbcUrl(env.getProperty("spring.datasource.url"));
        hikariConfig.setUsername(env.getProperty("spring.datasource.username"));
        hikariConfig.setPassword(env.getProperty("spring.datasource.password"));
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("prepStmtCacheSize", "250");
        dataSourceProperties.setProperty("cachePrepStmts", "true");
        dataSourceProperties.setProperty("prepStmtCacheSqlLimit", "2048");
        dataSourceProperties.setProperty("useServerPrepStmts", "true");
        hikariConfig.setDataSourceProperties(dataSourceProperties);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        //properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("javax.persistence.schema-generation.database.action", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql"));
        properties.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
        properties.setProperty("hibernate.enable_lazy_load_no_trans", env.getProperty("spring.jpa.enable_lazy_load_no_trans"));
        return properties;
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

    @Bean
    public RestTemplate createRestTemplate() {
        return new RestTemplate(createRequestFactory());
    }
}
