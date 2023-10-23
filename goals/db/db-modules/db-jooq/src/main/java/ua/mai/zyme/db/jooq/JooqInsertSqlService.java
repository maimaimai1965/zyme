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
 * Примеры INSERT операций в JOOQ.
 *   <i>insertOneNoReturn()</i> - метод вставляющий одну запись в таблицу, используя <i>insertInto()</i>, <i>set()</i>.
 *   <i>insertOneWithReturnRecord()</i> - метод вставляющий одну запись в таблицу и возвращающий вставленную запись,
 *        используя <i>insertInto()</i>, <i>set()</i>.
 *   <i>insertOneWithReturnId()</i> - метод вставляющий одну запись в таблицу, используя <i>insertInto()</i>,
 *        <i>set()</i>, и возвращающий идентификатор вставленной записи..
 *   <i>insertOneWithReturnRecordVariant2()</i> - метод вставляющий запись в таблицу и возвращающий вставленную запись.
 *        2-й вариант записи, использующий <i>insertInto()</i> с указанием таблицы перечислением колонок и values() с
 *        перечислением значений.
 *   <i>insertOneWithNewAndStore()</i> - метод, вставляющий запись в таблицу используя <i>newRecord()</i> и <i>store()</i>.
 *   <i>insertRecordsFromSelect()</i> - метод вставляющий записи, полученные через SELECT.
 *   <i>insertRecordsWithFor()</i> - метод, вставляющий записи в таблицу в цикле (отдельные INSERT).
 * </pre>
 */
@Service
@Transactional
public class JooqInsertSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqInsertSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqInsertSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
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

        // 1.
        dslContext.insertInto(AA_TRANS)
                  .set(AA_TRANS.TRANS_NAME, transName)
                  .set(AA_TRANS.NOTE, note)
                  .set(AA_TRANS.CREATED_DT, createdDt)
                  .execute();
        // 2.
        dslContext.insertInto(AA_TRANS, AA_TRANS.TRANS_NAME, AA_TRANS.NOTE, AA_TRANS.CREATED_DT)
                  .values(transName, note, createdDt)
                  .execute();
        // 3.
        dslContext.insertInto(AA_TRANS)
                  .columns(AA_TRANS.TRANS_NAME, AA_TRANS.NOTE, AA_TRANS.CREATED_DT)
                  .values(transName, note, createdDt)
                  .execute();
        // 4.
        AaTransRecord record = dslContext.newRecord(AA_TRANS);
        record.setTransName(transName);
        record.setNote(note);
        record.setCreatedDt(createdDt);
        record.store();

        LOG.debug("insertOneNoReturn [{}] 2: transId={}; transName='{}'; note={}; createdDt={}",
                schema, "?", transName, note == null ? "null" : "'" + note + "'", createdDt);
    }

    /**
     * <pre>
     * Метод вставляющий одну запись в таблицу (используя insertInto(), set()) и возвращающий вставленную запись.
     *
     * SQL оператор INSERT:
     *   insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?)
     *
     * Для возвращения записи JOOQ использует дополнительный SELECT:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   where `aa_trans`.`TRANS_ID` in (?)
     * </pre>
     */
    @Transactional
    public AaTransRecord insertOneWithReturnRecord(String transName, String note, LocalDateTime createdDt) {

        AaTransRecord record = dslContext
                .insertInto(AA_TRANS)
                    .set(AA_TRANS.TRANS_NAME, transName)
                    .set(AA_TRANS.NOTE, note)
                    .set(AA_TRANS.CREATED_DT, createdDt)
                .returning()
                .fetchOne();

        LOG.debug("insertOneWithReturnRecord [{}]: transId={}; transName='{}'; note={}; createdDt={}",
                schema, record.getTransId(), transName, note == null ? "null" : "'" + note + "'", createdDt);
        return record;
    }

    /**
     * <pre>
     * Метод вставляющий одну запись в таблицу (используя <i>insertInto()</i>, <i>set()</i>) и возвращающий
     * идентификатор вставленной записи.
     *
     * SQL оператор INSERT:
     *   insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?)
     *
     * Для возвращения идентификатора JOOQ <b>не использует</b> дополнительный SELECT.
     * </pre>
     */
    @Transactional
    public Long insertOneWithReturnId(String transName, String note, LocalDateTime createdDt) {

        Long id = dslContext
                .insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.NOTE, note)
                .set(AA_TRANS.CREATED_DT, createdDt)
                .returning(AA_TRANS.TRANS_ID)
                .fetchOne(AA_TRANS.TRANS_ID);

        LOG.debug("insertOneWithReturnId [{}]: transId={}", schema, id);
        return id;
    }

    /**
     * <pre>
     * Метод вставляющий запись в таблицу  и возвращающий вставленную запись. 2-й вариант записи - использует
     * <i>insertInto()</i> с указанием таблицы перечислением колонок и <i>values()</i> с перечислением значений.

     * SQL оператор INSERT:
     *   insert into `aa_trans` (`TRANS_NAME`, `CREATED_DT`) values (?, ?)

     * Для возвращения записи JOOQ использует дополнительный SELECT:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   where `aa_trans`.`TRANS_ID` in (?)
     * </pre>
     */
    @Transactional
    public AaTransRecord insertOneWithReturnRecordVariant2(String transName, String note, LocalDateTime createdDt) {

        AaTransRecord record = dslContext
                .insertInto(AA_TRANS, AA_TRANS.TRANS_NAME, AA_TRANS.NOTE, AA_TRANS.CREATED_DT)
                .values(transName, note, createdDt)
                .returning()
                .fetchOne();
        LOG.debug("insertOneWithReturnRecordVariant2 [{}] : transId={}; transName='{}'; note={}; createdDt={}",
                schema, record.getTransId(), transName, note == null ? "null" : "'" + note + "'", createdDt);
        return record;
    }

    /**
     * <pre>
     * Метод, вставляющий запись в таблицу используя newRecord() и store().
     * После выполнения store() поле в записи с автогенерируемым значением (transId) содержит значение.
     *
     * SQL оператор INSERT:
     *   insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?)
     * </pre>
     */
    @Transactional
    public void insertOneWithNewAndStore(String transName, String note, LocalDateTime createdDt) {
    
        AaTransRecord record = dslContext.newRecord(AA_TRANS);
        record.setTransName(transName);
        record.setNote(note);
        record.setCreatedDt(createdDt);
        record.store();

        LOG.debug("insertOneWithNewAndStore [{}] : transId={}; transName='{}'; note={}; createdDt={}",
                schema, record.getTransId(), transName, note == null ? "null" : "'" + note + "'", createdDt);
    }

    /**
     * <pre>
     * Метод вставляющий записи, полученные через SELECT.
     * Возвращается количество вставленных записей.
     *
     * SQL оператор INSERT:
     * insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`)
     *   select concat(`aa_trans`.`TRANS_NAME`, '_copy'),
     *          `aa_trans`.`NOTE`,
     *          cast(current_timestamp() as datetime(0))
     *   from `aa_trans`
     *   order by `aa_trans`.`TRANS_ID` asc
     *   limit 4
     *
     * </pre>
     */
    @Transactional
    public int insertRecordsFromSelect(int rowCount) {

        int insCount = dslContext
                .insertInto(AA_TRANS)
                .columns(AA_TRANS.TRANS_NAME,
                         AA_TRANS.NOTE,
                         AA_TRANS.CREATED_DT)
                .select(dslContext
                                .select(DSL.concat(AA_TRANS.TRANS_NAME, DSL.inline("_copy")),
                                        AA_TRANS.NOTE,
                                        DSL.now().cast(AA_TRANS.CREATED_DT)
                                        )
                                .from(AA_TRANS)
                                .orderBy(AA_TRANS.TRANS_ID.asc())
                                .limit(rowCount)
                        )
                .execute();

        LOG.debug("insertFromSelect [{}]: insert {} rows", schema, insCount);
        return insCount;
    }

    /**
     * Метод, вставляющий записи в таблицу в цикле (отдельные INSERT).
     */
    @Transactional
    public void insertRecordsWithFor(String transName, String note, LocalDateTime createdDt, int count) {
        assert(count > 0);

        for (int i = 1; i<=count; i++ ) {
            insertOneNoReturn(transName, note + " " + i, createdDt);
        }
    }

}
