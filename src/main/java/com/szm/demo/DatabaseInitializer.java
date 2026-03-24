package com.szm.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@ConfigurationProperties("init.datasource")
public class DatabaseInitializer implements CommandLineRunner {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private String url;
    private String username;
    private String password;
    private String databaseName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public void run(String... args) {

        initDatabase();

        String fullUrl = url.substring(0,url.indexOf("?"))+databaseName+url.substring(url.indexOf("?"));
        try (Connection conn = DriverManager.getConnection(fullUrl, username, password)) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("init.sql"));
            logger.info("Database initial completed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS " + databaseName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(sql);
            logger.info("Database [{}] created.", databaseName);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Create database failed", e);
        }
    }
}
