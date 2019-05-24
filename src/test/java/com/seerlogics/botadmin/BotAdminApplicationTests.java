package com.seerlogics.botadmin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://www.baeldung.com/spring-boot-tomcat-connection-pool
 * https://spring.io/guides/gs/testing-web/
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BotAdminApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void confirmDataSource() {
        assertThat(dataSource).isNotNull();
        assertEquals(dataSource.getClass().getName(), "com.zaxxer.hikari.HikariDataSource");
    }

    @Test
    public void contextLoads() {
    }
}
