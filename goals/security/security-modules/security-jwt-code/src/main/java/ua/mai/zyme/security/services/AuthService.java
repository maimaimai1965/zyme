package ua.mai.zyme.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.security.utils.GenerateCodeUtil;

import java.sql.Types;

@Service
@Transactional
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger("Authentication");

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AuthService(
            @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для H2 zyme03)
            @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для H2 zyme03)
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * @param username
     * @return code
     */
    @Transactional(readOnly = false)
    public String authByOtp(String username) {
        return renewOtp(username);
    }

    @Transactional(readOnly = true)
    public boolean checkOtp(String username, String code) {
        String dbCode = findCode(username);
        return (dbCode != null) && dbCode.equals(code);
    }

    private String findCode(String username) {
        String sql = "select code from otp where username = :p_username";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_username", new SqlParameterValue(Types.VARCHAR, username));

        String code = null;
        try {
            code = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
        }

        return code;
    }

    private String renewOtp(String username) {
        String code = GenerateCodeUtil.generateCode();
        String dbCode = findCode(username);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_username", new SqlParameterValue(Types.VARCHAR, username))
                .addValue("p_code", new SqlParameterValue(Types.VARCHAR, code));
        String sql = (dbCode == null)
              ? "insert into otp (username, code) values (:p_username, :p_code)"
              : "update otp set code = :p_code where username = :p_username";
        namedParameterJdbcTemplate.update(sql, namedParameters);
        LOG.debug("UserService: create code '{}' for user '{}'", code, username);

        return code;
    }

}
