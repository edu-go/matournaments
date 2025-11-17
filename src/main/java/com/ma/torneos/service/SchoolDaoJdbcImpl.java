package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.School;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

    @Override
    public List<School> findAll() {
        String sql = """
                SELECT school_id, owner_user_id, name, city, contact_phone
                FROM school
                ORDER BY name
                """;
        List<School> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateOwner(Long schoolId, Long ownerUserId) {
        if (ownerUserId == null) {
            throw new IllegalArgumentException("owner_user_id no puede ser null en updateOwner (columna NOT NULL).");
        }

        String sql = "UPDATE school SET owner_user_id = ? WHERE school_id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (ownerUserId == null) {
                ps.setNull(1, Types.BIGINT);
            } else {
                ps.setLong(1, ownerUserId);
            }
            ps.setLong(2, schoolId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error actualizando dueño de escuela: " + e.getMessage(), e);
        }
    }

    @Override
    public School insert(School s) {
        String sql = "INSERT INTO school(owner_user_id, name, city, contact_phone) VALUES (?, ?, ?, ?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // owner_user_id es NOT NULL en la BD
            if (s.getOwnerUserId() == null) {
                throw new IllegalArgumentException("owner_user_id no puede ser null al insertar una escuela.");
            }
            ps.setLong(1, s.getOwnerUserId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getCity());
            ps.setString(4, s.getContactPhone());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    // Si querés setear el ID en el objeto:
                    // s.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error insertando escuela: " + e.getMessage(), e);
        }
        return s;
    }

    private School mapRow(ResultSet rs) throws SQLException {
        return new School(
                rs.getLong("school_id"),
                rs.getObject("owner_user_id") != null ? rs.getLong("owner_user_id") : null,
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("contact_phone")
        );
    }
}
