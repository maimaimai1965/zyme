package ua.mai.zyme.db.jooq;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры UPDATE операций в JOOQ.
 *   <i>update()</i> - метод обновляющий записи в таблице по условию WHERE используя <i>update()</i>.
 *   <i>updateWithForUpdateAndStore()</i> - метод обновляющий записи в таблице с использованием <i>forUpdate()</i> и
 *       <i>store()</i>.
 * </pre>
 */
@Service
@Transactional
public class JooqUpdateSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqUpdateSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqUpdateSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    /**
     * <pre>
     * Метод обновляющий записи в таблице по условию WHERE используя update().
     *
     * SQL оператор UPDATE:
     *   update `aa_trans` set `aa_trans`.`TRANS_NAME` = ?, `aa_trans`.`NOTE` = ?, `aa_trans`.`CREATED_DT` = ? where `aa_trans`.`TRANS_ID` = ?
     * </pre>
     */
    @Transactional
    public int update(long transId, String transName, String note, LocalDateTime createdDt) {

        // 1.
        int count1 = dslContext.update(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.NOTE, note)
                .set(AA_TRANS.CREATED_DT, createdDt)
                .where(AA_TRANS.TRANS_ID.eq(transId))
                .execute();
        // 2.
        int count2 = dslContext.update(AA_TRANS)
                .set(DSL.row(AA_TRANS.TRANS_NAME, AA_TRANS.NOTE, AA_TRANS.CREATED_DT),
                     DSL.row(transName, note, createdDt))
                .where(AA_TRANS.TRANS_ID.eq(transId))
                .execute();

        LOG.debug("update [{}]: transId={}; transName='{}'; note={}; createdDt={}",
                schema, transId, transName, note == null ? "null" : "'" + note + "'", createdDt);
        return count2;
    }


    /**
     * <pre>
     * Метод обновляющий записи в таблице с использованием forUpdate() и store().
     *
     * SQL оператор UPDATE:
     *   update `aa_trans` set `aa_trans`.`TRANS_NAME` = ?, `aa_trans`.`NOTE` = ?, `aa_trans`.`CREATED_DT` = ? where `aa_trans`.`TRANS_ID` = ?
     * </pre>
     */
    @Transactional
    public int updateWithForUpdateAndStore(long transId, String transName, String note, LocalDateTime createdDt) {

         AaTransRecord record = dslContext.selectFrom(AA_TRANS)
                .where(AA_TRANS.TRANS_ID.eq(transId)) // Условие возвращающее не более одной записи.
                .forUpdate()
                .fetchAny();

        if (record != null) {
            record.setTransName(transName);
            record.setNote(note);
            record.setCreatedDt(createdDt);
            record.store();
            LOG.debug("updateWithForUpdateAndStore [{}]: transId={}; transName='{}'; note={}; createdDt={}",
                    schema, transId, transName, note == null ? "null" : "'" + note + "'", createdDt);
            return 1;
        } else {
            LOG.debug("updateWithForUpdateAndStore [{}]: record wirth transId={} not exost", schema, "?", transName);
            return 0;
        }
    }

}
