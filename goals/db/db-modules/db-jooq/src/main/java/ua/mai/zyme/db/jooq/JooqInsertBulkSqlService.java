package ua.mai.zyme.db.jooq;

import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep4;
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

import static ua.mai.zyme.db.aa.schema.Tables.AA_ROLE;
import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры INSERT операций в JOOQ, использующие bulk - один оператор INSERT.
 *
 * Bulk SQL оператор INSERT:
 *   insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`)
 *   values (?, ?, ?), ..., (?, ?, ?)
 *
 *   <i>insertRecordListWithBulk()</i> - метод, вставляющий список записей в таблицу, используя bulk.
 *   <i>insertRecordsWithBulkThrowSqlException()</i> - метод, в котором при вставке списка записей в таблицу
 *     используется bulk и при этом возникает SQL ошибка.
 *
 * </pre>
 */
@Service
@Transactional
public class JooqInsertBulkSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqInsertBulkSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqInsertBulkSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }


    /**
     * <pre>
     * Метод, вставляющий список записей в таблицу, используя bulk.
     *
     * Bulk SQL оператор INSERT:
     * 1. С указанием колонок не полностью соответствующим колонкам таблицы:
     *   insert into `aa_trans` (`TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?), ..., (?, ?, ?)
     * 1. С указанием колонок полностью соответствующим колонкам таблицы:
     *   insert into `aa_trans` (`TRANS_ID`, `TRANS_NAME`, `NOTE`, `CREATED_DT`) values (?, ?, ?, ?), ..., (?, ?, ?, ?)
     * </pre>
     */
    @Transactional
    public int insertRecordListWithBulk(String transName, String note, LocalDateTime createdDt, int count) {
        LOG.debug("insertRecordListWithBulk [{}]: insert {} records", schema, count);
        List<AaTransRecord> list = generateAaTransRecord(transName, note, createdDt, count);

        // 1. С указанием колонок не полностью соответствующим колонкам таблицы
        InsertValuesStep3 insertValuesStep = dslContext
                .insertInto(AA_TRANS)
                .columns(AA_TRANS.TRANS_NAME, AA_TRANS.NOTE, AA_TRANS.CREATED_DT);
        list.forEach(record -> insertValuesStep.values(record.getTransName(), record.getNote(), record.getCreatedDt()));
        int returnCount = insertValuesStep.execute();

        // 2. С указанием колонок полностью соответствующим колонкам таблицы
        returnCount = dslContext
                .insertInto(AA_TRANS)
                .columns(AA_TRANS.fields())
                .valuesOfRecords(list)
                .execute();

        returnCount = dslContext
                .insertInto(AA_TRANS)
                .columns(AA_TRANS.fields())
                .valuesOfRecords(list)
                .execute();

        return returnCount;
    }

    /**
     * <pre>
     * Метод, в котором при вставке списка записей в таблицу используется Bulk и при этом возникает SQL ошибка.
     * Логируется обрезанное сообщение об ошибке к которому добавляется сообщение об SQL ошибке, являющееся причиной.
     * </pre>
     */
    @Transactional
    public int insertRecordsWithBulkThrowSqlException() {
        List<AaRoleRecord> list = new ArrayList<>();
        list.add(new AaRoleRecord(null, "ROLE_CD_BULK_1", "ROLE_NAME_BULK_1", "A", null));
        list.add(new AaRoleRecord(null, "ROLE_CD_BULK_2", "ROLE_NAME_BULK_2", "A", null));
        // Ошибочная запись, т.к. поле ROLE_CD - UNIQUE
        list.add(new AaRoleRecord(null, "ROLE_CD_BULK_1", "ROLE_NAME_BULK_3", "A", null));

        try {
            InsertValuesStep4 insertValuesStep = dslContext.insertInto(AA_ROLE)
                    .columns(AA_ROLE.ROLE_CD, AA_ROLE.ROLE_NAME, AA_ROLE.ROLE_TYPE, AA_ROLE.NOTE);
            list.forEach(record -> insertValuesStep.values(record.getRoleCd(), record.getRoleName(), record.getRoleType(), record.getNote()));

            return insertValuesStep.execute();
        }
        catch (Exception e) {
            // Не нужно использовать error() с передачей ошибки e, т.к. она уже выводится в лог логером org.jooq.tools.LoggerListener:
            // LOG.error("insertRecordsWithBatchThrowException [{}]: exception", e);

            // e.getMessage() может возвращать строку очень большого размера (bulk может исарользоваться для вставки
            // миллионов строк), поэтому выводится сообщение обрезанное до MESSAGE_CLIPPED_SIZE символов и к нему
            // добавляется соообщение об SQL ошибке.
            LOG.error("insertRecordsWithBulkThrowException [{}] exception: {}", schema, getMessageClipped(e.getMessage(), e.getCause()));
            return 0;
        }
    }

    private List<AaTransRecord> generateAaTransRecord(String transName, String note, LocalDateTime createdDt, int count) {
        assert(count > 0);
        List<AaTransRecord> list = new ArrayList<>();

        for (int i = 1; i<=count; i++ ) {
            list.add(new AaTransRecord(null, transName, note + " " + i, createdDt));
        }
        return list;
    }

    public static final int MESSAGE_CLIPPED_SIZE = 100;

    public static String getMessageClipped(String message, Throwable causeException) {
        return getMessageClipped(message, MESSAGE_CLIPPED_SIZE, causeException);
    }

    public static String getMessageClipped(String message, int clippedSize, Throwable causeException) {
        if (message == null || clippedSize <= 0 || clippedSize > message.length())
            return message;
        if (message.startsWith("SQL [") && causeException != null) {
            Throwable cause = causeException.getCause() != null ? causeException.getCause() : causeException;
            return message.substring(0, clippedSize - 3) + "...: " + cause.getMessage();
        } else
            return message.substring(0, clippedSize - 3) + "...";
    }

}
