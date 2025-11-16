package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.Role;
import com.ma.torneos.domain.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final JdbcConfig jdbcConfig;

    public AuthService(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        // We compare using MySQL's SHA2 to match the seeded hashes (UNHEX(SHA2(...,256)))
        final String sql = """
            SELECT user_id, username, role, email
            FROM user_account
            WHERE username = ?
              AND password_hash = UNHEX(SHA2(?, 256))
            """;

        try (Connection cn = DriverManager.getConnection(
                jdbcConfig.getJdbcUrl(), jdbcConfig.getJdbcUser(), jdbcConfig.getJdbcPass());
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, rawPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Long id = rs.getLong("user_id");
                String u = rs.getString("username");
                Role role = Role.valueOf(rs.getString("role"));
                String email = rs.getString("email");
                return Optional.of(new User(id, u, role, email));
            }
        } catch (SQLException e) {
            // For a demo, print a simple dialog message could be useful; but keep logic here
            e.printStackTrace();
            return null;
        }
    }
}
