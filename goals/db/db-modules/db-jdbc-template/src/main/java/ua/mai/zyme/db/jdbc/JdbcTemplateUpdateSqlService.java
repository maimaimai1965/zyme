package ua.mai.zyme.db.jdbc;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
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
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры UPDATE операций для <i>JdbcTemplate</i> и <i>NamedParameterJdbcTemplate</i>.
 *
 *   <i>update()</i> - метод обновляющий записи в таблице по условию WHERE используя <i>update()</i>.
 * </pre>
 */
@Service
public class JdbcTemplateUpdateSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateUpdateSqlService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JdbcTemplateUpdateSqlService(
            @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
            @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для zyme)
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * <pre>
     * Метод обновляющий записи в таблице по условию WHERE используя update().
     *
     * SQL оператор UPDATE:
     *   update aa_trans set TRANS_NAME = ?, NOTE = ?, CREATED_DT = ? where TRANS_ID = ?
     * </pre>
     */
    @Transactional
    public int update(long transId, String transName, String note, LocalDateTime createdDt) {

        // 1. jdbcTemplate
        String sql1 = "update aa_trans set TRANS_NAME = ?, NOTE = ?, CREATED_DT = ? where TRANS_ID = ?";
        int count1 = jdbcTemplate.update(sql1, transName + " jdbcTemplate", note, createdDt, transId);
        LOG.debug("update() [{}] 1.jdbcTemplate", schema);

        // 2. namedParameterJdbcTemplate + MapSqlParameterSource
        String sql2 = "update aa_trans set TRANS_NAME = :p_trans_name, NOTE = :p_note, CREATED_DT = :p_created_dt where TRANS_ID = :p_trans_id";
        SqlParameterSource namedParameters2 = new MapSqlParameterSource()
                .addValue("p_trans_id", transId)
                .addValue("p_trans_name", transName + " jdbcTemplate")
                .addValue("p_note", note)
                .addValue("p_created_dt", createdDt);
        int count2 = namedParameterJdbcTemplate.update(sql2, namedParameters2);
        LOG.debug("update() [{}] 2.namedParameterJdbcTemplate", schema);

        return count2;
    }

}
