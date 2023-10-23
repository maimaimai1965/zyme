package ua.mai.zyme.db.jdbc;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
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
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры INSERT операций для <i>JdbcTemplate</i> и <i>NamedParameterJdbcTemplate</i>.
 *
 *   <i>insertOneNoReturn()</i> - метод вставляющий одну запись в таблицу.
 * </pre>
 */
@Service
public class JdbcTemplateInsertSqlService {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateInsertSqlService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JdbcTemplateInsertSqlService(
                 @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
                 @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для zyme)
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * <pre>
     * Метод вставляющий одну запись в таблицу.
     *
     * SQL оператор INSERT:
     *   insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?)
     * </pre>
     */
    @Transactional
    public void insertOneNoReturn(String transName, String note, LocalDateTime createdDt) {

        // 1. jdbcTemplate
        String sql1 = "insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?)";
        jdbcTemplate.update(sql1, transName + " jdbcTemplate", note, createdDt);
        LOG.debug("insertOneNoReturn() [{}] 1.jdbcTemplate", schema);

        // 2. namedParameterJdbcTemplate + MapSqlParameterSource
        String sql2 = "insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (:p_trans_name, :p_note, :p_created_dt)";
        SqlParameterSource namedParameters2 = new MapSqlParameterSource()
                .addValue("p_trans_name", transName + " namedParameterJdbcTemplate + MapSqlParameterSource")
                .addValue("p_note", note)
                .addValue("p_created_dt", createdDt);
        namedParameterJdbcTemplate.update(sql2, namedParameters2);
        LOG.debug("insertOneNoReturn() [{}] 2.namedParameterJdbcTemplate", schema);

        // 3. namedParameterJdbcTemplate + BeanPropertySqlParameterSource
        String sql3 = "insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (:transName, :note, :createdDt)";
        AaTrans aaTrans = new AaTrans(null, transName + " namedParameterJdbcTemplate + BeanPropertySqlParameterSource", note, createdDt);
        SqlParameterSource namedParameters3 = new BeanPropertySqlParameterSource(aaTrans);
        namedParameterJdbcTemplate.update(sql3, namedParameters3);
        LOG.debug("insertOneNoReturn() [{}] 3.namedParameterJdbcTemplate", schema);

    }

}
