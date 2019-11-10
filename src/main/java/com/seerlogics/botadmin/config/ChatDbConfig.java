package com.seerlogics.botadmin.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bkane on 3/11/19.
 * https://medium.com/@joeclever/using-multiple-datasources-with-spring-boot-and-spring-data-6430b00c02e7
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        transactionManagerRef = "chatBotTransactionManager",
        entityManagerFactoryRef = "chatBotEntityManagerFactory",
        basePackages = {"com.seerlogics.chatbot.repository"}
)
public class ChatDbConfig {

    @Value("${chatbot.datasource.hibernate.ddl-auto:update}")
    private String hibernateHbm2ddlValue;

    @Value("${chatbot.datasource.hibernate.jdbc.time_zone:UTC}")
    private String hibernateJDBCTimezone;

    @Value("${chatbot.datasource.hibernate.show_sql:false}")
    private Boolean hibernateShowSQL;

    @Bean(name = "chatBotDataSource")
    @ConfigurationProperties(prefix = "chatbot.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "chatBotEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("chatBotDataSource") DataSource dataSource
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", this.hibernateHbm2ddlValue);
        properties.put("hibernate.jdbc.time_zone", this.hibernateJDBCTimezone);
        properties.put("hibernate.show_sql", this.hibernateShowSQL);

        return builder
                .dataSource(dataSource)
                .packages("com.seerlogics.chatbot")
                .persistenceUnit("chatbot")
                .properties(properties)
                .build();
    }

    @Bean(name = "chatBotTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("chatBotEntityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
