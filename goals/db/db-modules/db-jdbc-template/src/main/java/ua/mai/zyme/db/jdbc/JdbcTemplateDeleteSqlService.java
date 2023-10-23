package ua.mai.zyme.db.jdbc;

import org.jooq.DSLContext;
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

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры DELETE операций для <i>JdbcTemplate</i> и <i>NamedParameterJdbcTemplate</i>.
 *
 *   <i>delete()</i> - метод удаляющий запись в таблице по условию WHERE.
 * </pre>
 */
@Service
public class JdbcTemplateDeleteSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateDeleteSqlService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JdbcTemplateDeleteSqlService(
            @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
            @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для zyme)
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * <pre>
     * Метод удаляющий запись в таблице по условию WHERE.
     *
     * SQL:
     * delete from actor where id = ? and created_dt < ?
     * </pre>
     */
    @Transactional
    public int delete() {
        long transId = 1L;
        LocalDateTime createdDt = LocalDateTime.of(2020, Month.JANUARY, 1, 14, 0);

        // 1. jdbcTemplate
        String sql1 = "delete from aa_trans where trans_id = ? and created_dt < ?";
        int count1 = jdbcTemplate.update(sql1, transId, createdDt);
        LOG.debug("delete() [{}] 1.jdbcTemplate: count = {}", schema, count1);

        // 2. namedParameterJdbcTemplate
        String sql2 = "delete from aa_trans where trans_id = :p_trans_id and created_dt < :p_created_dt";
        SqlParameterSource namedParameters2 = new MapSqlParameterSource()
                .addValue("p_trans_id", transId)
                .addValue("p_created_dt", createdDt);
        int count2 = namedParameterJdbcTemplate.update(sql2, namedParameters2);
        LOG.debug("delete() [{}] 2.namedParameterJdbcTemplate: count = {}", schema, count2);

        return count1;
    }

}
