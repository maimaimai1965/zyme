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
 * Примеры DELETE операций в JOOQ.
 *   <i>delete()</i> - метод удаляющий запись в таблице по условию WHERE.
 *   <i>deleteWithRowIn()</i> - метод удаляющий запись в таблице по условию WHERE, содержащем проверку с использованием
 *       <i>row().in(select(...))</i>.
 * </pre>
 */
@Service
@Transactional
public class JooqDeleteSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqDeleteSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqDeleteSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    /**
     * <pre>
     * Метод удаляющий запись в таблице по условию WHERE.
     *
     * SQL:
     * delete from `aa_trans` where `aa_trans`.`TRANS_ID` = ?
     * </pre>
     */
    @Transactional
    public int delete() {
        long transId = 1L;

        int count = dslContext.delete(AA_TRANS)
                .where(AA_TRANS.TRANS_ID.eq(transId))
                .execute();
        LOG.debug("delete() [{}]: count={}", schema, count);
        return count;
    }

    /**
     * <pre>
     * Метод удаляющий запись в таблице по условию WHERE, содержащем проверку с использованием <i>row().in(select(...))</i>.
     *
     * SQL:
     * delete from `aa_trans`
     * where (`aa_trans`.`TRANS_NAME`, `aa_trans`.`CREATED_DT`) in (
     *           select * from (select `aa_trans`.`TRANS_NAME`, `aa_trans`.`CREATED_DT`
     *                          from `aa_trans` where `aa_trans`.`TRANS_ID` = ?) as `t`)
     * </pre>
     */
    @Transactional
    public int deleteWithRowIn() {
        long transId = 1L;

        int count = dslContext.delete(AA_TRANS)
                .where(DSL.row(AA_TRANS.TRANS_NAME, AA_TRANS.CREATED_DT).in(
                        dslContext.select(AA_TRANS.TRANS_NAME, AA_TRANS.CREATED_DT)
                                  .from(AA_TRANS)
                                  .where(AA_TRANS.TRANS_ID.eq(transId)))
                 )
                .execute();
        LOG.debug("delete [{}]: transId={}", schema, count);
        return count;
    }

}
