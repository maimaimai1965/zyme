package ua.mai.zyme.db.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <pre>
 * Примеры SELECT операций для <i>JdbcTemplate</i> и <i>NamedParameterJdbcTemplate</i>.
 *
 *   <i>selectOneColumnOneRow()</i> - чтение одного поля для одной записи используя <i>jdbcTemplate</i> и <i>namedParameterJdbcTemplate</i>.
 *   <i>selectOneRow()</i> - чтение одной записи используя <i>jdbcTemplate</i> и <i>namedParameterJdbcTemplate</i>.
 *   <i>selectManyRows()</i> - чтение нескольких записей используя <i>jdbcTemplate</i> и <i>namedParameterJdbcTemplate</i>.
 * </pre>
 */
@Service
public class JdbcTemplateSelectSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateSelectSqlService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JdbcTemplateSelectSqlService(@Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
                                        @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для zyme)
                                    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * <pre>
     * Чтение одного поля для одной записи используя <i>jdbcTemplate</i> и <i>namedParameterJdbcTemplate</i>.
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectOneColumnOneRow() {
        // 1.
        String sql1 =
        """
            select trans_id
            from aa_trans
            limit 1
        """;
        int transId1 = jdbcTemplate.queryForObject(sql1, Integer.class);
        LOG.debug("selectOneColumnOneRow() [{}] 1.: transId={}", schema, transId1);

        // 2. With named parameters as MapSqlParameterSource.
        String sql2 =
        """
            select trans_id
            from aa_trans
            where trans_id > :p_trans_id
              and created_dt > :p_created_dt
            limit 1
        """;
        SqlParameterSource namedParameters2 = new MapSqlParameterSource()
                .addValue("p_trans_id", 1)
                .addValue("p_created_dt", LocalDateTime.of(2000, Month.JANUARY, 1, 14, 00));
        int transId2 = namedParameterJdbcTemplate.queryForObject(sql2, namedParameters2, Integer.class);
        LOG.debug("selectOneColumnOneRow() [{}] 2.: transId={}", schema, transId2);
    }

    /**
     * <pre>
     * Чтение одной записи используя <i>jdbcTemplate</i> и <i>namedParameterJdbcTemplate</i>.
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectOneRow() {
        // 1.
        String sql1 =
                """
                    select *
                    from aa_trans
                    limit 1
                """;
        AaTrans aaTrans1 = jdbcTemplate.queryForObject(sql1, new AaTransRowMapper());
        LOG.debug("selectOneRow() [{}] 1.jdbcTemplate: {}", schema, aaTrans1);

        // 2. With named parameters as MapSqlParameterSource.
        String sql2 =
                """
                    select *
                    from aa_trans
                    where trans_id > :p_trans_id
                      and created_dt > :p_created_dt
                    limit 1
                """;
        SqlParameterSource namedParameters2 = new MapSqlParameterSource()
                .addValue("p_trans_id", 1)
                .addValue("p_created_dt", LocalDateTime.of(2000, Month.JANUARY, 1, 14, 00));
        AaTrans aaTrans2 = namedParameterJdbcTemplate.queryForObject(sql2, namedParameters2, new AaTransRowMapper());
        LOG.debug("selectOneRow() [{}] 2.namedParameterJdbcTemplate: {}", schema, aaTrans2);
    }

    /**
     * <pre>
     * Чтение нескольких записей используя <i>jdbcTemplate</i> и <i>namedParameterJdbcTemplate</i>.
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectManyRows() {
        // 1.
        String sql1 =
                """
                    select *
                    from aa_trans
                    limit 3
                """;
        List<AaTrans> list1 = jdbcTemplate.query(sql1, new AaTransRowMapper());
        LOG.debug("selectManyRows() [{}] 1.jdbcTemplate: count={}", schema, list1.size());

        // 2. With named parameters as MapSqlParameterSource.
        String sql2 =
                """
                    select *
                    from aa_trans
                    where trans_id > :p_trans_id
                      and created_dt > :p_created_dt
                    limit 3
                """;
        SqlParameterSource namedParameters2 = new MapSqlParameterSource()
                .addValue("p_trans_id", 1)
                .addValue("p_created_dt", LocalDateTime.of(2000, Month.JANUARY, 1, 14, 00));
        List<AaTrans> list2 = namedParameterJdbcTemplate.query(sql2, namedParameters2, new AaTransRowMapper());
        LOG.debug("selectManyRows() [{}] 2.namedParameterJdbcTemplate: count={}", schema, list2.size());
    }

}
