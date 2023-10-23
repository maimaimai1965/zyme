package ua.mai.zyme.db.jooq;

import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.records.AaRoleRecord;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры INSERT операций в JOOQ, использующие batch().
 *   <i>insertRecordsWithBatchUsingRecordList()</i> - метод, вставляющий список записей в таблицу используя batch(). Для
 *        вставки используется список объектов TableRecord - dslContext.batchInsert(list).execute().
 *   <i>insertRecordsWithBatchDefiningNeededFields()</i> - метод, вставляющий записи в таблицу используя batch (в вызове
 *        через <i>set()</i> определяем список полей для вставки).
 *   <i>insertRecordsWithBatchDefiningNeededFieldsVariant2()</i> - метод вставляющий записи в таблицу используя batch (в
 *        вызове определяем список полей для вставки - <i>values()</i>).
 *   <i>insertRecordsWithBatchThrowException()</i> - метод в котором при вставке списка записей в таблицу используется
 *        <i>batch()</i> и при этом возникает SQL ошибка.
 * </pre>
 */
@Service
@Transactional
public class JooqInsertBatchSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqInsertBatchSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqInsertBatchSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }


    /**
     * Метод, вставляющий список записей в таблицу используя batch.
     * Для вставки используется список объектов TableRecord - dslContext.batchInsert(list).execute().
     */
    @Transactional
    public int [] insertRecordsWithBatchUsingRecordList(String transName, String note, LocalDateTime createdDt, int count) {
        List<AaTransRecord> list = generateAaTransRecord(transName, note, createdDt, count);

        int [] ar = dslContext.batchInsert(list).execute();

        LOG.debug("insertRecordsWithBatchUsingRecordList [{}]: inserted {} records", schema, count);
        return ar;
    }

    /**
     * Метод вставляющий записи в таблицу используя batchInsert().
     */
    @Transactional
    public int [] insertRecordsWithBatchDefiningNewRecords(String transName, String note, LocalDateTime createdDt, int count)
    {
        List<AaTransRecord> list = generateAaTransRecord(transName, note, createdDt, count);

        int[] ar = dslContext.batchInsert(list).execute();

        LOG.debug("insertRecordsWithBatchDefiningNewRecords [{}]: inserted {} records", schema, count);
        return ar;
    }


    /**
     * Метод, вставляющий записи в таблицу используя batch (в вызове через set() определяем список полей для вставки).
     */
    @Transactional
    public int [] insertRecordsWithBatchDefiningNeededFields(String transName, String note, LocalDateTime createdDt, int count) {
        List<AaTransRecord> list = generateAaTransRecord(transName, note, createdDt, count);

        BatchBindStep batchBindStep = dslContext.batch(
                // Определяем шаблон для вставки
                dslContext.insertInto(AA_TRANS)
                        .set(AA_TRANS.TRANS_NAME, (String)null)
                        .set(AA_TRANS.NOTE, (String)null)
                        .set(AA_TRANS.CREATED_DT, (LocalDateTime)null)
        );

        for (AaTransRecord record: list)
            batchBindStep.bind(record.getTransName(), record.getNote(), record.getCreatedDt());

        int [] ar = batchBindStep.execute();

        LOG.debug("insertRecordsWithBatchDefiningNeededFields [{}]: inserted {} records", schema, count);
        return ar;
    }

    /**
     * Метод вставляющий записи в таблицу используя batch (в вызове определяем список полей для вставки).
     * Вариант 2 - использует insertInto() с указанием таблицы перечислением колонок и values() с перечислением значений.
     */
    @Transactional
    public int [] insertRecordsWithBatchDefiningNeededFieldsVariant2(String transName, String note, LocalDateTime createdDt, int count)
    {
        List<AaTransRecord> list = generateAaTransRecord(transName, note, createdDt, count);

        BatchBindStep batchBindStep = dslContext.batch(
                // Определяем шаблон для вставки
                dslContext.insertInto(AA_TRANS, AA_TRANS.TRANS_NAME, AA_TRANS.NOTE, AA_TRANS.CREATED_DT)
                        // Нужно приводить null значения к соответствующим им типам полей в insertInto()
                        .values((String)null, (String)null, (LocalDateTime)null)
        );

        for (AaTransRecord record: list)
            batchBindStep.bind(record.getTransName(), record.getNote(), record.getCreatedDt());

        int [] ar = batchBindStep.execute();

        LOG.debug("insertRecordsWithBatchDefiningNeededFieldsVariant2 [{}]: inserted {} records", schema, count);
        return ar;
    }

    /**
     * Метод, в котором при вставке списка записей в таблицу используется <i>batch()</i> и при этом возникает SQL ошибка.
     */
    @Transactional
    public int [] insertRecordsWithBatchThrowException() {
        List<AaRoleRecord> list = new ArrayList<>();
        list.add(new AaRoleRecord(null, "ROLE_CD_1", "ROLE_NAME_1", "A", null));
        list.add(new AaRoleRecord(null, "ROLE_CD_2", "ROLE_NAME_1", "A", null));
        // Ошибочная запись, т.к. поле ROLE_CD - UNIQUE
        list.add(new AaRoleRecord(null, "ROLE_CD_1", "ROLE_NAME_1", "A", null));

        int[] ar = null;
        try {
            ar = dslContext.batchInsert(list).execute();
        }
        catch (Exception e) {
            LOG.error("insertRecordsWithBatchThrowException [{}] exception: {}", e.getMessage());
            // Не нужно в error() передовать ошибку e, т.к. она уже выводится в лог логером org.jooq.tools.LoggerListener:
            // LOG.error("insertRecordsWithBatchThrowException [{}]: exception", e);
        }
        return ar;
    }

    private List<AaTransRecord> generateAaTransRecord(String transName, String note, LocalDateTime createdDt, int count) {
        assert(count > 0);
        List<AaTransRecord> list = new ArrayList<>();

        for (int i = 1; i<=count; i++ ) {
            list.add(new AaTransRecord(null, transName, note + " " + i, createdDt));
        }
        return list;
    }

}
