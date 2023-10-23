package ua.mai.zyme.db.jooq;

import org.jooq.*;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры SELECT операций в JOOQ, использующих нативный SQL.
 *
 *   <i>selectNativeSqlIntoList()</i> - SELECT из нативного SQL, и помещение считанных строк в список объектов.
 * </pre>
 */
@Service
@Transactional
public class JooqSelectNativeSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqSelectNativeSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqSelectNativeSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    private static final String sqlNativeQuery1 =
    """
        select TRANS_ID, TRANS_NAME, NOTE, CREATED_DT
            from aa_trans
            limit 10
    """;
    /**
     * <pre>
     * SELECT из нативного SQL, и помещение считанных строк в список объектов:
     *   1. Создаваемый объект задается классом <i>AaTransRecord.class</i>. В новом объекте инициализируются только те
     *      поля, названия которых совпадают с названиями полей в SQL запросе.
     *   2. Объект создается через конструктор с явным заполнением полей используя <i>with()</i>.
     *   3. Создаваемый объект задается классом <i>AnotherTrans.class</i>. В новом объекте инициализируются только те
     *      поля, названия которых совпадают с названиями полей в SQL запросе.
     *   4. Объект создается через конструктор с перечислением получаемых значений из запроса.
     *
     * SQL:
     *   select `aa_trans`.`TRANS_NAME` from `aa_trans`
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectNativeSqlIntoList() {

        // 1. В новом объекте инициализируются только те поля, названия которых совпадают с названиями полей в SQL запросе.
        List<AaTransRecord> list1 = dslContext
                .fetch(sqlNativeQuery1)
                .into(AaTransRecord.class);
        LOG.debug("selectNativeSqlIntoList into list of AaTransRecord [{}]: count={}", schema, list1.size());


        // 2.
        List<AaTransRecord> list2 = dslContext
                .fetch(sqlNativeQuery1)
                .stream().map(record ->
                     new AaTransRecord()
                             .with(AA_TRANS.TRANS_ID,   record.getValue("TRANS_ID", Long.class))
                             .with(AA_TRANS.TRANS_NAME, record.getValue("TRANS_NAME", String.class))
                             .with(AA_TRANS.NOTE,       record.getValue("NOTE", String.class))
                             .with(AA_TRANS.CREATED_DT, record.getValue("CREATED_DT", LocalDateTime.class))
                ).collect(Collectors.toList());
        LOG.debug("selectNativeSql into list of AaTransRecord [{}]: count={}", schema, list2.size());


        // 3. Создаваемый объект задается классом AnotherTrans.class. В новом объекте инициализируются только те
        //    поля, названия которых совпадают с названиями полей в SQL запросе.
        List<AnotherTrans> list3 = dslContext
                .fetch(sqlNativeQuery1)
                .into(AnotherTrans.class);
        LOG.debug("selectNativeSqlIntoList into list of AnotherTrans [{}]: count={}", schema, list3.size());

        // 4. Объект создается через конструктор с перечислением получаемых значений из запроса.
        List<AnotherTrans> list4 = dslContext
                .fetch(sqlNativeQuery1)
                .stream().map(record ->
                    new AnotherTrans(record.getValue("TRANS_ID", Long.class),
                                     record.getValue("TRANS_NAME", String.class),
                                     record.getValue("CREATED_DT", LocalDateTime.class),
                                     0)
                ).collect(Collectors.toList());
        LOG.debug("selectNativeSql into list of AnotherTrans [{}]: count={}", schema, list4.size());
    }

    private static final String sqlNativeQuery2 =
            """
                select TRANS_ID, TRANS_NAME, NOTE, CREATED_DT
                    from aa_trans
                    limit 31
            """;
    /**
     * <pre>
     * SELECT записей в MySQL из нативного SQL используя <i>fetchLazy()</i>:
     * 1. Извлекает <b>весь</b> <i>ResultSet</i> одновременно и затем jOOQ сканирует полученный <i>ResultSet</i> запись
     *    за записью с помощью <i>fetchNext()</i> в цикле <i>while</i>.
     * 2. Извлекает <b>весь</b> <i>ResultSet</i> одновременно и затем jOOQ сканирует полученный <i>Cursor(ResultSet)</i>
     *    запись за записью в <i>цикле for</i>.
     * 3. Извлекает <b>весь</b> <i>ResultSet</i> одновременно и затем jOOQ сканирует полученный <i>ResultSet</i> через
     *    <i>fetchNext(int)</i> по 10 записей за раз.
     * 4. Извлекает <i>ResultSet</i> <b>из 15 строк</b> за раз из курсора базы данных и затем jOOQ сканирует полученный
     *    <i>ResultSet</i> через <i>fetchNext(int)</i> <b>по 6 записей</b> за раз
     *
     * SQL:
     * select *
     * from (select TRANS_ID, TRANS_NAME, NOTE, CREATED_DT
     *       from aa_trans
     *       limit 31) t
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectNativeSqlIntoListFetchLazy() {

        // 1. По умолчанию MySQL JDBC извлекает весь ResultSet одновременно.
        //    Затем jOOQ сканирует полученный ResultSet запись за записью с помощью fetchNext() в цикле while.
        List<Record> list1 = new ArrayList<>();
        try (Cursor<Record> cursor1 =  dslContext
                .selectFrom("(" + sqlNativeQuery2 + ") t")
                .fetchLazy()) {
            while (cursor1.hasNext()) {
                Record record1 = cursor1.fetchNext();
                list1.add(record1);
            }
        }
        LOG.debug("selectNativeSqlIntoListFetchLazy() fetchLazy().fetchNext() One by One into List via while [{}] size={}", schema, list1.size());

        // 2. По умолчанию MySQL JDBC извлекает все записи в ResultSet одновременно.
        //    Затем jOOQ сканирует полученный Cursor(ResultSet) в цикле for.
        List<Record> list2 = new ArrayList<>();
        try (Cursor<Record> cursor2 = dslContext
                .selectFrom("(" + sqlNativeQuery2 + ") t")
                .fetchLazy()) {
            for (Record record2 : cursor2) {
                list2.add(record2);
            }
        }
        LOG.debug("selectNativeSqlIntoListFetchLazy() fetchLazy().fetchNext() One by One into List via for [{}] size={}", schema, list2.size());

        // 3. По умолчанию MySQL JDBC извлекает все записи в ResultSet одновременно.
        //    Затем jOOQ сканирует полученный ResultSet через fetchNext(int) — здесь 10 записей за раз.
        List<Record> list3 = new ArrayList<>();
        try (Cursor<Record> cursor3 = dslContext
                .selectFrom("(" + sqlNativeQuery2 + ") t")
                .fetchLazy()) {
            while (cursor3.hasNext()) {
                List<Record> currList3 = cursor3.fetchNext(10);
                list3.addAll(currList3);
            }
        }
        LOG.debug("selectNativeSqlIntoListFetchLazy() fetchLazy().fetchNext(10) into List via while [{}] size={}", schema, list3.size());

        // 4. Указывает MySQL JDBC извлекать ResultSet из 15 строк за раз из курсора базы данных.
        //    Затем jOOQ сканирует полученный ResultSet через fetchNext(int) — здесь 6 записей за раз
        List<Record> list4 = new ArrayList<>();
        try (Cursor<Record> cursor4 = dslContext
                .selectFrom("(" + sqlNativeQuery2 + ") t")
                .resultSetType(ResultSet.TYPE_FORWARD_ONLY)
                .resultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
                .fetchSize(15)
                .fetchLazy()) {
            while (cursor4.hasNext()) {
                List<Record> currList4 = cursor4.fetchNext(6);
                list4.addAll(currList4);
            }
        }
        LOG.debug("selectNativeSqlIntoListFetchLazy() fetchSize(15).fetchLazy()+fetchNext(6) into List via while [{}] size={}", schema, list4.size());
    }

}
