package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.ui.RegistrationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDaoJdbcImpl implements RegistrationDao {
    private final JdbcConfig jdbcConfig;

    public RegistrationDaoJdbcImpl() {
        this.jdbcConfig = new JdbcConfig();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConfig.getJdbcUrl(), jdbcConfig.getJdbcUser(), jdbcConfig.getJdbcPass());
    }

    @Override
    public int countActiveByDivisionId(Long divisionId) {
        String sql = """
                SELECT COUNT(*) 
                FROM registration
                WHERE division_id = ?
                  AND status IN ('PENDING','CONFIRMED')
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, divisionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<RegistrationView> findByDivisionId(Long divisionId) {
        String sql = """
                SELECT r.registration_id,
                       c.competitor_id,
                       CONCAT(c.last_name, ', ', c.first_name) AS competitor_name,
                       r.status
                FROM registration r
                JOIN competitor c ON c.competitor_id = r.competitor_id
                WHERE r.division_id = ?
                ORDER BY competitor_name
                """;
        List<RegistrationView> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, divisionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RegistrationView(
                            rs.getLong("registration_id"),
                            rs.getLong("competitor_id"),
                            rs.getString("competitor_name"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateStatus(Long registrationId, String newStatus) {
        String sql = "UPDATE registration SET status = ? WHERE registration_id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setLong(2, registrationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long insertRegistration(Long tournamentId, Long competitorId) {
        String sql = """
            INSERT INTO registration (tournament_id, competitor_id, division_id, status)
            VALUES (?, ?, NULL, 'PENDING')
            """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, tournamentId);
            ps.setLong(2, competitorId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        } catch (SQLException e) {
            // si hay constraint único (tournament_id, competitor_id) fallará aquí
            e.printStackTrace();
            throw new RuntimeException("Error insertando inscripción: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean existsRegistration(Long tournamentId, Long competitorId) {
        String sql = """
            SELECT COUNT(*) 
            FROM registration
            WHERE tournament_id = ?
              AND competitor_id = ?
            """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, tournamentId);
            ps.setLong(2, competitorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
