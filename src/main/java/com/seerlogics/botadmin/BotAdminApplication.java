package com.seerlogics.botadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.Executor;

/**
 * READ THESE LINKS to understant @SpringBootApplication:
 * https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-using-springbootapplication-annotation.html#using-boot-using-springbootapplication-annotation
 * https://springbootdev.com/2017/11/13/what-are-the-uses-of-entityscan-and-enablejparepositories-annotations/
 *
 * Since ALL the JPA repository and Entities and controllers are under 'com.seerlogics.botadmin',
 * the "@SpringBootApplication's" auto configuration is able to do component scans
 */
@SpringBootApplication // same as adding @Configuration @EnableAutoConfiguration @ComponentScan
@EnableAsync
// https://stackoverflow.com/questions/43480147/how-to-block-cassandra-from-trying-to-connect-automatically/43481903
@EnableAutoConfiguration(exclude = {
        CassandraDataAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class,
        JmsAutoConfiguration.class,
        ActiveMQAutoConfiguration.class})
@EnableTransactionManagement
public class BotAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotAdminApplication.class, args);
    }

    @Bean(name = "launchBotTaskExecutor")
    public Executor launchBotTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("LaunchBot");
        executor.initialize();
        return executor;
    }

    @Bean(name = "stopBotTaskExecutor")
    public Executor stopBotTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("StopBot");
        executor.initialize();
        return executor;
    }
}
