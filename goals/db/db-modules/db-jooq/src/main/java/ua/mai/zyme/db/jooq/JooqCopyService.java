package ua.mai.zyme.db.jooq;

import org.jooq.BatchBindStep;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;
import ua.mai.zyme.db.config.JdbcConfigurationZyme02Settings;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Примеры копирования данных из одной схемы в другую.
 *
 *   <i>copySelectAndInsertWithSize()</i> - копирования <i>limit</i> строк из таблицы <i>zyme.AA_TRANS</i> в таблицу в
 *      другой БД - <i>zyme02.AA_TRANS</i>. Считывание и запись осуществляется порциями по <i>batchSize</i> строк.
 * </pre>
 */
@Service
@Transactional
public class JooqCopyService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqCopyService.class);


    //------------ Объекты настроены для zyme (Default): --------------------
    // Дефолтный transactionTemplate (управляемый дефолтным transactionManager) и дефолтный dslContext (БД zyme):
    private final DSLContext dslContextZyme;
    private final TransactionTemplate transactionTemplateZyme;
    // transactionManager используется для создания новых TransactionTemplate
    private final PlatformTransactionManager transactionManagerZyme;

    //------------ Объекты настроены для zyme02: --------------------
    private final DSLContext dslContextZyme02;
    private final TransactionTemplate transactionTemplateZyme02;
    // transactionManager используется для создания новых TransactionTemplate
    private final PlatformTransactionManager transactionManagerZyme02;
    //---------------------------------------------------------------

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JooqCopyService(
            @Autowired DSLContext dslContext,                         // дефолтный dslContext (для zyme)
            @Autowired TransactionTemplate transactionTemplate,       // дефолтный transactionTemplate (для zyme)
            @Autowired PlatformTransactionManager transactionManager, // дефолтный PlatformTransactionManager (для zyme)
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.DSL_CONTEXT_ZYME02) DSLContext dslContextZyme02,                                 // свой DSLContext для zyme02
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_TEMPLATE_ZYME02) TransactionTemplate transactionTemplateZyme02,      // свой TransactionTemplate для zyme02
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02) PlatformTransactionManager transactionManagerZyme02  // свой PlatformTransactionManager для zyme02
    ) {
        this.dslContextZyme = dslContext;
        this.transactionTemplateZyme = transactionTemplate;
        this.transactionManagerZyme = transactionManager;

        this.dslContextZyme02 = dslContextZyme02;
        this.transactionTemplateZyme02 = transactionTemplateZyme02;
        this.transactionManagerZyme02 = transactionManagerZyme02;
    }

    /**
     * <pre>
     * Копирования <i>limit</i> строк из таблицы <i>zyme.AA_TRANS</i> в таблицу в другой БД - <i>zyme02.AA_TRANS</i>.
     * Считывание и запись осуществляется порциями по <i>batchSize</i> строк.
     * Для выборки данных используются новый <i>TransactionTemplate</i> (управляемый дефолтным <i>transactionManagerZyme</i>).
     * Выборка осуществляется через <i>Cursor</i> порциями размером <i>batchSize</i> строк за раз в <i>ResultSet</i> -
     * <i>fetchSize(batchSize)</i>. Из <i>ResultSet</i> считывается сразу все <i>batchSize</i> строк в список -
     * <i>cursor.fetchNext(batchSize)</i>.
     * Для вставки используются новый <i>TransactionTemplate</i> (управляемый своим <i>transactionManagerZyme02</i>),
     * который выполняется в новой транзакции. Вставка всего очереднего списка порции осуществляется с использованием
     * <i>batch()</i>.
     * </pre>
     */
    public void copySelectAndInsertWithSize(final int limit, final int batchSize) {
        // Источник данных находится в zyme. Выборку данных оборачиваем в новый TransactionTemplate:
        TransactionTemplate templateZyme = new TransactionTemplate(transactionManagerZyme);
        templateZyme.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        templateZyme.setReadOnly(true);

        templateZyme.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    TransactionTemplate templateZyme02 = new TransactionTemplate(transactionManagerZyme02);
                    templateZyme02.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

                    int batchNum = 0;
                    int count = 0;
                    try (Cursor<AaTransRecord> cursor = dslContextZyme
                            .selectFrom(AA_TRANS)
                            .limit(limit)
                            .resultSetType(ResultSet.TYPE_FORWARD_ONLY)
                            .resultSetConcurrency(ResultSet.CONCUR_READ_ONLY)
                            .fetchSize(batchSize)  // извлекать ResultSet размером batchSize строк за раз из курсора базы данных.
                            .fetchLazy()) {
                        while (cursor.hasNext()) {
                            List<AaTransRecord> currList = cursor.fetchNext(batchSize);

                            batchNum++;
                            count += currList.size();
                            for (int i =0; i < currList.size(); i++)
                                currList.get(i).setNote("copy: batch " + batchNum + " record " + (i + 1));
                            LOG.debug("copySelectAndInsertWithSize: batch {} - read {} record(s) from zyme.AA_TRANS (generally read {} records) ",
                                    batchNum, currList.size(), count);

                            insertListInNewTransaction(templateZyme02, currList);
                        }
                    }

                    LOG.debug("copySelectAndInsertWithSize: read {} records from zyme.AA_TRANS", count);
                } catch (Throwable e) {
                    LOG.error("copySelectAndInsertWithSize: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    private void insertListInNewTransaction(TransactionTemplate templateZyme02, List<AaTransRecord> list) {
        templateZyme02.execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    BatchBindStep batchBindStep = dslContextZyme02.batch(
                            dslContextZyme02.insertInto(ua.mai.zyme.db.aa02.schema.tables.AaTrans.AA_TRANS)
                                    .set(ua.mai.zyme.db.aa02.schema.tables.AaTrans.AA_TRANS.TRANS_NAME, (String)null)
                                    .set(ua.mai.zyme.db.aa02.schema.tables.AaTrans.AA_TRANS.NOTE, (String)null)
                                    .set(ua.mai.zyme.db.aa02.schema.tables.AaTrans.AA_TRANS.CREATED_DT, (LocalDateTime)null)
                    );

                    list.forEach(record -> batchBindStep.bind(record.getTransName(), record.getNote(), record.getCreatedDt()));
                    //То же самое, только с for:
                    //for (AaTransRecord record: list)
                    //    batchBindStep.bind(record.getTransName(), record.getNote(), record.getCreatedDt());

                    int [] ar = batchBindStep.execute();

                    LOG.debug("copySelectAndInsertWithSize: wrote {} records into zyme02.AA_TRANS", list.size());
                } catch (Throwable e) {
                    LOG.error("copySelectAndInsertWithSize: Run SQL Error during insert", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

}
