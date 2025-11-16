package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.Tournament;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TournamentDaoJdbcImpl implements TournamentDao {
    private final JdbcConfig jdbcConfig;

    public TournamentDaoJdbcImpl() {
        this.jdbcConfig = new JdbcConfig();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                jdbcConfig.getJdbcUrl(),
                jdbcConfig.getJdbcUser(),
                jdbcConfig.getJdbcPass());
    }

    @Override
    public List<Tournament> findAll() {
        String sql = """
                SELECT tournament_id, name, start_date, location, ruleset, status
                FROM tournament
                ORDER BY start_date DESC, name
                """;
        List<Tournament> list = new ArrayList<>();
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
    public Optional<Tournament> findById(Long id) {
        String sql = """
                SELECT tournament_id, name, start_date, location, ruleset, status
                FROM tournament WHERE tournament_id = ?
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Tournament insert(Tournament t) {
        String sql = """
                INSERT INTO tournament(name, start_date, location, ruleset, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setDate(2, Date.valueOf(t.getStartDate()));
            ps.setString(3, t.getLocation());
            ps.setString(4, t.getRuleset());
            ps.setString(5, t.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void update(Tournament t) {
        String sql = """
                UPDATE tournament
                SET name = ?, start_date = ?, location = ?, ruleset = ?, status = ?
                WHERE tournament_id = ?
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setDate(2, Date.valueOf(t.getStartDate()));
            ps.setString(3, t.getLocation());
            ps.setString(4, t.getRuleset());
            ps.setString(5, t.getStatus());
            ps.setLong(6, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM tournament WHERE tournament_id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsByNameAndDate(String name, LocalDate date, Long excludeId) {
        String sql = """
                SELECT COUNT(*) 
                FROM tournament 
                WHERE name = ? AND start_date = ?
                """ + (excludeId != null ? " AND tournament_id <> ?" : "");

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(date));
            if (excludeId != null) {
                ps.setLong(3, excludeId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Tournament> findOpen() {
        String sql = """
            SELECT tournament_id, name, start_date, location, ruleset, status
            FROM tournament
            WHERE status = 'OPEN'
            ORDER BY start_date, name
            """;
        List<Tournament> list = new ArrayList<>();
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

    private Tournament mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("tournament_id");
        String name = rs.getString("name");
        LocalDate date = rs.getDate("start_date").toLocalDate();
        String location = rs.getString("location");
        String ruleset = rs.getString("ruleset");
        String status = rs.getString("status");
        return new Tournament(id, name, date, location, ruleset, status);
    }
}
