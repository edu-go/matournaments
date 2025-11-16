package com.ma.torneos.service;

import com.ma.torneos.config.JdbcConfig;
import com.ma.torneos.domain.Competitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompetitorDaoJdbcImpl implements CompetitorDao {
    private final JdbcConfig jdbcConfig;

    public CompetitorDaoJdbcImpl() {
        this.jdbcConfig = new JdbcConfig();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcConfig.getJdbcUrl(), jdbcConfig.getJdbcUser(), jdbcConfig.getJdbcPass());
    }

    @Override
    public Optional<Competitor> findById(Long id) {
        String sql = """
                SELECT competitor_id, school_id, first_name, last_name,
                       birthdate, gender, belt_level, weight_kg
                FROM competitor
                WHERE competitor_id = ?
                """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                Long competitorId = rs.getLong("competitor_id");
                Long schoolId = rs.getLong("school_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                LocalDate birthdate = rs.getDate("birthdate").toLocalDate();
                String gender = rs.getString("gender");
                String beltLevel = rs.getString("belt_level");
                double weightKg = rs.getDouble("weight_kg");

                return Optional.of(new Competitor(
                        competitorId, schoolId, firstName, lastName,
                        birthdate, gender, beltLevel, weightKg
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Competitor> findBySchoolId(Long schoolId) {
        String sql = """
            SELECT competitor_id, school_id, first_name, last_name,
                   birthdate, gender, belt_level, weight_kg
            FROM competitor
            WHERE school_id = ?
            ORDER BY last_name, first_name
            """;
        List<Competitor> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, schoolId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Competitor(
                            rs.getLong("competitor_id"),
                            rs.getLong("school_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getDate("birthdate").toLocalDate(),
                            rs.getString("gender"),
                            rs.getString("belt_level"),
                            rs.getDouble("weight_kg")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
