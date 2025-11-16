package com.ma.torneos.config;

public class JdbcConfig {
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPass;

    public JdbcConfig() {
        this.jdbcUrl  = "jdbc:mysql://localhost:3306/martial_tournament?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        this.jdbcUser = "appuser";
        this.jdbcPass = "apppass";
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public String getJdbcPass() {
        return jdbcPass;
    }
}
