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

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
// https://stackoverflow.com/questions/43480147/how-to-block-cassandra-from-trying-to-connect-automatically/43481903
@EnableAutoConfiguration(exclude = {
        CassandraDataAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class,
        JmsAutoConfiguration.class,
        ActiveMQAutoConfiguration.class})
//@ComponentScan(basePackages = {"com.lingoace", "com.dowaltz"})
//@EnableJpaRepositories(basePackages = {"com.lingoace", "com.dowaltz"})
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
}
