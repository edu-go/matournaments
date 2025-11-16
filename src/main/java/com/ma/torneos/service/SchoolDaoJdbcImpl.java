package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.School;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SchoolDaoJdbcImpl implements SchoolDao {
    private final JdbcConfig jdbcConfig;

    public SchoolDaoJdbcImpl() {
        this.jdbcConfig = new JdbcConfig();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConfig.getJdbcUrl(), jdbcConfig.getJdbcUser(), jdbcConfig.getJdbcPass());
    }

    @Override
    public List<School> findByOwnerUserId(Long ownerUserId) {
        String sql = """
                SELECT school_id, owner_user_id, name, city, contact_phone
                FROM school
                WHERE owner_user_id = ?
                ORDER BY name
                """;
        List<School> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, ownerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new School(
                            rs.getLong("school_id"),
                            rs.getLong("owner_user_id"),
                            rs.getString("name"),
                            rs.getString("city"),
                            rs.getString("contact_phone")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
