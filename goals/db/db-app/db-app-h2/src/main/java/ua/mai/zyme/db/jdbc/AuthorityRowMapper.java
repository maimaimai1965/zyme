package ua.mai.zyme.db.jdbc;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorityRowMapper implements RowMapper<Authority> {
    @Override
    public Authority mapRow(ResultSet rs, int rowNum) throws SQLException {
        Authority authority = new Authority();

        authority.setUsername(rs.getString("USERNAME"));
        authority.setAuthority(rs.getString("AUTHORITY"));
        return authority;
    }
}
