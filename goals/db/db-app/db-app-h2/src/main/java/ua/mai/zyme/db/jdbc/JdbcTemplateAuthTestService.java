package ua.mai.zyme.db.jdbc;

import org.jooq.exception.TooManyRowsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Тестирование SQL запросов, используемых в Authentication.
 */
@Service
public class JdbcTemplateAuthTestService {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateAuthTestService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme03";

    public JdbcTemplateAuthTestService(
                 @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
                 @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для zyme)
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * <pre>
     * Метод проверяющий запрос
     *     select username, password, 1 as enabled from h2_user where username = ?
     * </pre>
     */
    @Transactional
    public User selectUser(String username) {
        String sql = "select username, password, 1 as enabled from h2_user where username = ?";
        List<User> userList = jdbcTemplate.query(sql,
                new Object[] { username },
                new int[] { Types.VARCHAR },
                new UserRowMapper());
        if (userList.size() > 1) {
            LOG.debug("selectUser() [{}] Too many rows for username {}", schema, username);
            throw new TooManyRowsException();
        } else
        if (userList.size() == 1) {
            User user = userList.get(0);
            LOG.debug("selectUser() [{}] Got User for username {}", schema, username);
            return user;
        } else {
            LOG.debug("selectUser() [{}] Not find User for username {}", schema, username);
            return null;
        }
    }

    /**
     * <pre>
     * Метод проверяющий запрос
     *     select username, authority from authority where username = ?
     * </pre>
     */
    @Transactional
    public Authority selectAuthority(String username) {
        String sql = "select username, authority from authority where username = ?";
        List<Authority> userList = jdbcTemplate.query(sql,
                new Object[] { username },
                new int[] { Types.VARCHAR },
                new AuthorityRowMapper());
        if (userList.size() > 1) {
            LOG.debug("selectAuthority() [{}] Too many rows for username {}", schema, username);
            throw new TooManyRowsException();
        } else
        if (userList.size() == 1) {
            Authority authority = userList.get(0);
            LOG.debug("selectAuthority() [{}] Got Authority for username {}", schema, username);
            return authority;
        } else {
            LOG.debug("selectAuthority() [{}] Not find Authority for username {}", schema, username);
            return null;
        }

    }

}
