package ua.mai.zyme.db.jooq;

import org.jooq.*;
import org.jooq.exception.TooManyRowsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры SELECT операций в JOOQ, использующих <i>Cursor</i>:
 *
 *   <i>selectFetchLazy()</i> - чтение записей используя <i>fetchLazy()</i>.
 * </pre>
 */
@Service
@Transactional
public class JooqSelectCursorSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqSelectCursorSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqSelectCursorSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    /**
     * <pre>
     * Чтение записей в MySQL используя <i>fetchLazy()</i>:
     * 1. Извлекает <b>весь</b> <i>ResultSet</i> одновременно и затем jOOQ сканирует полученный <i>ResultSet</i> запись
     *    за записью с помощью <i>fetchNext()</i> в цикле <i>while</i>.
     * 2. Извлекает <b>весь</b> <i>ResultSet</i> одновременно и затем jOOQ сканирует полученный <i>Cursor(ResultSet)</i>
     *    запись за записью в <i>цикле for</i>.
     * 3. Извлекает <b>весь</b> <i>ResultSet</i> одновременно и затем jOOQ сканирует полученный <i>ResultSet</i> через
     *    <i>fetchNext(int)</i> по 10 записей за раз.
     * 4. Извлекает <i>ResultSet</i> <b>из 15 строк</b> за раз из курсора базы данных и затем jOOQ сканирует полученный
     *    <i>ResultSet</i> через <i>fetchNext(int)</i> <b>по 6 записей</b> за раз
     *
     * SQL оператор SELECT:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   limit 31
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectFetchLazy() {
        SelectQuery queryManyRecords = dslContext
                .selectFrom(AA_TRANS)
                .limit(31)
                .getQuery();

        // 1. По умолчанию MySQL JDBC извлекает весь ResultSet одновременно.
        //    Затем jOOQ сканирует полученный ResultSet запись за записью с помощью fetchNext() в цикле while.
        List<AaTransRecord> list1 = new ArrayList<>();
        try (Cursor<AaTransRecord> cursor1 = queryManyRecords.fetchLazy()) {
            while (cursor1.hasNext()) {
                AaTransRecord record1 = cursor1.fetchNext();
                list1.add(record1);
            }
        }
        LOG.debug("selectFetchLazy fetchLazy().fetchNext() One by One into List via while [{}] size={}", schema, list1.size());

        // 2. По умолчанию MySQL JDBC извлекает все записи в ResultSet одновременно.
        //    Затем jOOQ сканирует полученный Cursor(ResultSet) в цикле for.
        List<AaTransRecord> list2 = new ArrayList<>();
        try (Cursor<AaTransRecord> cursor2 = queryManyRecords.fetchLazy()) {
            for (AaTransRecord record2 : cursor2) {
                list2.add(record2);
            }
        }
        LOG.debug("selectFetchLazy fetchLazy().fetchNext() One by One into List via for [{}] size={}", schema, list2.size());

        // 3. По умолчанию MySQL JDBC извлекает все записи в ResultSet одновременно.
        //    Затем jOOQ сканирует полученный ResultSet через fetchNext(int) — здесь 10 записей за раз.
        List<AaTransRecord> list3 = new ArrayList<>();
        try (Cursor<AaTransRecord> cursor3 = queryManyRecords.fetchLazy()) {
            while (cursor3.hasNext()) {
                List<AaTransRecord> currList3 = cursor3.fetchNext(10);
                list3.addAll(currList3);
            }
        }
        LOG.debug("selectFetchLazy fetchLazy().fetchNext(10) into List via while [{}] size={}", schema, list3.size());

        // 4. Указывает MySQL JDBC извлекать ResultSet из 15 строк за раз из курсора базы данных.
        //    Затем jOOQ сканирует полученный ResultSet через fetchNext(int) — здесь 6 записей за раз
        List<AaTransRecord> list4 = new ArrayList<>();
        try (Cursor<AaTransRecord> cursor4 =
                     queryManyRecords
                             .resultSetType(ResultSet.TYPE_FORWARD_ONLY)
                             .resultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
                             .fetchSize(15)
                             .fetchLazy()) {
            while (cursor4.hasNext()) {
                List<AaTransRecord> currList4 = cursor4.fetchNext(6);
                list4.addAll(currList4);
            }
        }
        LOG.debug("selectFetchLazy fetchSize(15).fetchLazy()+fetchNext(6) into List via while [{}] size={}", schema, list4.size());
    }

}
