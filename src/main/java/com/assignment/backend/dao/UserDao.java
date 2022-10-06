package com.assignment.backend.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    @Autowired
    private NamedParameterJdbcTemplate template;

    public Integer findUserById(int id) {

        String query = "SELECT * FROM user WHERE user_id=:userId";
        final MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("userId", id);
        return template.query(query, source, rs ->  rs.next() ? rs.getObject("next_line_id", Integer.class) : null);
    }

    public Boolean ifUserByExistId(int id) {

        String query = "SELECT EXISTS (SELECT * FROM user WHERE user_id=:userId)";
        final MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("userId", id);
        return template.queryForObject(query, source, Boolean.class);
    }
}
