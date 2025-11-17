package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.Role;
import com.ma.torneos.domain.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJdbcImpl implements UserDao {
    private final JdbcConfig jdbcConfig;

    public UserDaoJdbcImpl() {
        this.jdbcConfig = new JdbcConfig();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConfig.getJdbcUrl(), jdbcConfig.getJdbcUser(), jdbcConfig.getJdbcPass());
    }

    @Override
    public List<User> findOwners() {
        String sql = """
                SELECT user_id, username, role, email
                FROM user_account
                WHERE role = 'OWNER'
                ORDER BY username
                """;
        List<User> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("user_id");
                String username = rs.getString("username");
                Role role = Role.valueOf(rs.getString("role"));
                String email = rs.getString("email");
                list.add(new User(id, username, role, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
