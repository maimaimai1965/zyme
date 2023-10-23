package ua.mai.zyme.db.jdbc;

import org.jooq.BatchBindStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.records.AaRoleRecord;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;
import ua.mia.zyme.common.DateUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры batch INSERT операций для <i>JdbcTemplate</i> и <i>NamedParameterJdbcTemplate</i>:
 *
 *   <i>insertBatchList()</i> - метод, вставляющий список объектов AaTrans в таблицу используя batchUpdate() для <i>JdbcTemplate</i>.
 *   <i>insertBatchListWithNamedParameter()</i> - метод, вставляющий список объектов AaTrans в таблицу используя
 *       batchUpdate() и named parameters для <i>NamedParameterJdbcTemplate</i>.
 * </pre>
 */
@Service
@Transactional
public class JdbcTemplateInsertBatchSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateInsertBatchSqlService.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JdbcTemplateInsertBatchSqlService(
            @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
            @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate  // дефолтный namedParameterJdbcTemplate (для zyme)
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    /**
     * Метод, вставляющий список объектов AaTrans в таблицу используя batchUpdate() для <i>JdbcTemplate</i>..
     */
    @Transactional
    public int [] insertBatchList(String transName, String note, LocalDateTime createdDt, int count) {
        List<AaTrans> list = generateAaTrans(transName, note, createdDt, count);

        String sql = "insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?)";
        int[] ar = jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, list.get(i).getTransName());
                        ps.setString(2, list.get(i).getNote());
                        ps.setTimestamp(3, DateUtil.toTimestamp(list.get(i).getCreatedDt()));
                    }

                    public int getBatchSize() {
                        return list.size();
                    }
                });
        LOG.debug("insertBatchList [{}]: inserted {} records", schema, count);
        return ar;
    }

    /**
     * Метод, вставляющий список объектов AaTrans в таблицу используя batchUpdate() и named parameters для
     * <i>NamedParameterJdbcTemplate</i>.
     */
    @Transactional
    public int [] insertBatchListWithNamedParameter(String transName, String note, LocalDateTime createdDt, int count) {
        List<AaTrans> list = generateAaTrans(transName, note, createdDt, count);

        String sql = "insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (:p_trans_name, :p_note, :p_created_dt)";
        List<MapSqlParameterSource> paramList = new ArrayList<>();
        for (AaTrans aaTrans: list) {
            MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("p_trans_name", aaTrans.getTransName())
                    .addValue("p_note", aaTrans.getNote())
                    .addValue("p_created_dt", aaTrans.getCreatedDt());
            paramList.add(namedParameters);
        }
        int[] ar = namedParameterJdbcTemplate.batchUpdate(sql, paramList.toArray(new MapSqlParameterSource[0]));
        LOG.debug("insertBatchListWithNamedParameter [{}]: inserted {} records", schema, count);
        return ar;
    }

//    /**
//     * Метод, в котором при вставке списка записей в таблицу используется <i>batch()</i> и при этом возникает SQL ошибка.
//     */
//    @Transactional
//    public int [] insertRecordsWithBatchThrowException() {
//        List<AaRoleRecord> list = new ArrayList<>();
//        list.add(new AaRoleRecord(null, "ROLE_CD_1", "ROLE_NAME_1", "A", null));
//        list.add(new AaRoleRecord(null, "ROLE_CD_2", "ROLE_NAME_1", "A", null));
//        // Ошибочная запись, т.к. поле ROLE_CD - UNIQUE
//        list.add(new AaRoleRecord(null, "ROLE_CD_1", "ROLE_NAME_1", "A", null));
//
//        int[] ar = null;
//        try {
//            ar = dslContext.batchInsert(list).execute();
//        }
//        catch (Exception e) {
//            LOG.error("insertRecordsWithBatchThrowException [{}] exception: {}", e.getMessage());
//            // Не нужно в error() передовать ошибку e, т.к. она уже выводится в лог логером org.jooq.tools.LoggerListener:
//            // LOG.error("insertRecordsWithBatchThrowException [{}]: exception", e);
//        }
//        return ar;
//    }

    private List<AaTrans> generateAaTrans(String transName, String note, LocalDateTime createdDt, int count) {
        assert(count > 0);
        List<AaTrans> list = new ArrayList<>();

        for (int i = 1; i<=count; i++ ) {
            list.add(new AaTrans(null, transName, note + " " + i, createdDt));
        }
        return list;
    }

}
