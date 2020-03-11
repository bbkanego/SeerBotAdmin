package com.seerlogics.botadmin.config;

import com.seerlogics.commons.config.HibernateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.tomcat.TomcatLoadTimeWeaver;
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
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
@ComponentScan(basePackages = {"com.seerlogics.commons.config"})
@EnableTransactionManagement
@EnableJpaRepositories(
        transactionManagerRef = "botAdminTransactionManager",
        entityManagerFactoryRef = "botAdminEntityManagerFactory",
        basePackages = {"com.seerlogics.commons.repository"}
)
public class BotAdminDbConfig implements LoadTimeWeavingConfigurer {

    @Autowired
    private HibernateConfig hibernateConfig;

    @Primary
    @Bean(name = "botAdminDataSource")
    @ConfigurationProperties(prefix = "botadmin.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "botAdminEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("botAdminDataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", this.hibernateConfig.getBotAdminHibernateHbm2ddlValue());
        properties.put("hibernate.jdbc.time_zone", this.hibernateConfig.getBotAdminHibernateJDBCTimezone());
        properties.put("hibernate.show_sql", this.hibernateConfig.getBotAdminHibernateShowSQL());
        properties.put("hibernate.physical_naming_strategy", this.hibernateConfig.getBotAdminNamingStrategy());
        properties.put("hibernate.dialect", this.hibernateConfig.getBotAdminHibernateDialect());

        return builder
                .dataSource(dataSource)
                .packages("com.seerlogics.commons.model")
                .persistenceUnit("botAdmin")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "botAdminTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("botAdminEntityManagerFactory") EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Override
    public LoadTimeWeaver getLoadTimeWeaver() {
        return new TomcatLoadTimeWeaver();
    }
}
