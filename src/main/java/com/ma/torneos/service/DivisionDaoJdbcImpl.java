package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.Division;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DivisionDaoJdbcImpl implements DivisionDao {

    private final JdbcConfig jdbcConfig;

    public DivisionDaoJdbcImpl() {
        this.jdbcConfig = new JdbcConfig();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConfig.getJdbcUrl(), jdbcConfig.getJdbcUser(), jdbcConfig.getJdbcPass());
    }

    @Override
    public List<Division> findByTournamentId(Long tournamentId) {
        String sql = """
                SELECT division_id, tournament_id, name,
                       age_min, age_max,
                       weight_min, weight_max,
                       belt_min, belt_max,
                       gender
                FROM division
                WHERE tournament_id = ?
                ORDER BY name
                """;
        List<Division> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Division insert(Division d) {
        String sql = """
                INSERT INTO division(
                    tournament_id, name,
                    age_min, age_max,
                    weight_min, weight_max,
                    belt_min, belt_max,
                    gender
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, d.getTournamentId());
            ps.setString(2, d.getName());
            ps.setInt(3, d.getAgeMin());
            ps.setInt(4, d.getAgeMax());
            ps.setDouble(5, d.getWeightMin());
            ps.setDouble(6, d.getWeightMax());
            ps.setString(7, d.getBeltMin());
            ps.setString(8, d.getBeltMax());
            ps.setString(9, d.getGender());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    d.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    @Override
    public void update(Division d) {
        String sql = """
                UPDATE division
                SET name = ?,
                    age_min = ?, age_max = ?,
                    weight_min = ?, weight_max = ?,
                    belt_min = ?, belt_max = ?,
                    gender = ?
                WHERE division_id = ?
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, d.getName());
            ps.setInt(2, d.getAgeMin());
            ps.setInt(3, d.getAgeMax());
            ps.setDouble(4, d.getWeightMin());
            ps.setDouble(5, d.getWeightMax());
            ps.setString(6, d.getBeltMin());
            ps.setString(7, d.getBeltMax());
            ps.setString(8, d.getGender());
            ps.setLong(9, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM division WHERE division_id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Division mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("division_id");
        Long tournamentId = rs.getLong("tournament_id");
        String name = rs.getString("name");
        int ageMin = rs.getInt("age_min");
        int ageMax = rs.getInt("age_max");
        double weightMin = rs.getDouble("weight_min");
        double weightMax = rs.getDouble("weight_max");
        String beltMin = rs.getString("belt_min");
        String beltMax = rs.getString("belt_max");
        String gender = rs.getString("gender");
        return new Division(id, tournamentId, name, ageMin, ageMax, weightMin, weightMax, beltMin, beltMax, gender);
    }
}
