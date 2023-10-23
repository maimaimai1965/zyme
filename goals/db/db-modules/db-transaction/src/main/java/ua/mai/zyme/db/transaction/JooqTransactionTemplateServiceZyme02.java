package ua.mai.zyme.db.transaction;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.aa02.schema.tables.records.AaTransRecord;
import ua.mai.zyme.db.config.JdbcConfigurationZyme02Settings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa02.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme02</i>, в котором:
 *   - используется JOOQ со <b>своим</b> <i>dslContextZyme02</i> (БД <i>zyme02</i>);
 *   - используется <b>свой глобальный</b> <i>transactionManagerZyme02</i>;
 *   - транзакционность обеспечивается <b>своим глобальным</b> <i>transactionTemplateZyme02</i> или <b>новым</b>
 *     <i>TransactionTemplate</i> (использующим <i>transactionManagerZyme02</i>).
 *
 * Методы:
 *
 * Сервис для работы с БД <i>zyme02</i>. Транзакционность обеспечивается функциональностью <i>TransactionTemplate</i>.
 * Используются <b>свой общий</b> <i>transactionManagerZyme02</i> (или <b>новый</b> <i>transactionManagerZyme02</i>) и
 * свой <i>dslContextZyme02</i>.
 *
 *   <i>zyme02TransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод <b>не возвращающий
 *     значения</b> и обновляющий данные в БД <i>zyme02</i>. Используются <b>свой общий</b> <i>transactionManagerZyme02</i>.
 *   <i>zyme02TransactionTemplate_TransactionCallback()</i> - транзакционный метод <b>возвращающий значение</b> и
 *     обновляющий данные в БД <i>zyme02</i>. Используются <b>свой общий</b> <i>transactionManagerZyme02</i>.
 *   <i>zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW</i> - транзакционный метод для тестирования
 *     вызова из него аннотированного транзакционного метода другого класса (который <b>будет транзакционным</b>).
 *   <i>newZyme02TransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод не возвращающий
 *     значение и обновляющий данные в БД <i>zyme02</i>. Используются <b>новый</b> <i>TransactionTemplate</i>.
 *   <i>newZyme02TransactionTemplate_TransactionCallback</i> - транзакционный метод возвращающий значение и обновляющий
 *     данные в БД <i>zyme02</i>. Используются <b>новый</b> <i>TransactionTemplate</i>.
 * </pre>
 */
@Service
public class JooqTransactionTemplateServiceZyme02 {

    private static final Logger LOG = LoggerFactory.getLogger(JooqTransactionTemplateServiceZyme02.class);

    //------------ Объекты настроены для zyme02: --------------------
    private final DSLContext dslContextZyme02;
    private final TransactionTemplate transactionTemplateZyme02;
    // transactionManager используется для создания новых TransactionTemplate
    private final PlatformTransactionManager transactionManager;
    //---------------------------------------------------------------

    // Используется только для информирования. В настройках не используется.
    private static final String schema = "zyme02";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JooqTransactionTemplateServiceZyme02(
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.DSL_CONTEXT_ZYME02) DSLContext dslContextZyme02,                             // свой DSLContext для zyme02
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_TEMPLATE_ZYME02) TransactionTemplate transactionTemplateZyme02,  // свой TransactionTemplate для zyme02
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02) PlatformTransactionManager transactionManager    // свой PlatformTransactionManager для zyme02
    ) {
        this.dslContextZyme02 = dslContextZyme02;
        this.transactionTemplateZyme02 = transactionTemplateZyme02;
        this.transactionManager = transactionManager;
    }

    /**
     * Транзакционный метод не возвращающий значения и обновляющий данные в БД zyme02.
     * Используются свой общий transactionTemplate (управляемый своим TransactionManager) и свой dslContext (БД zyme02).
     */
    public void zyme02TransactionTemplate_TransactionCallbackWithoutResult() {
        transactionTemplateZyme02.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02TransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("zyme02TransactionTemplate_TransactionCallbackWithoutResult [{}]: transId = {}", schema, record.getTransId());
                } catch (Throwable e) {
                    LOG.error("zyme02TransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme02.
     * Используются свой общий transactionTemplate (управляемый своим TransactionManager) и свой dslContext (БД zyme02).
     */
    public AaTransRecord zyme02TransactionTemplate_TransactionCallback() {
        AaTransRecord result = transactionTemplateZyme02.execute(new TransactionCallback<AaTransRecord>() {
            @Override
            public AaTransRecord doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02TransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("zyme02TransactionTemplate_TransactionCallback [{}]: transId = {}", schema, record.getTransId());
                    return record;
                } catch (Throwable e) {
                    LOG.error("zyme02TransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * <pre>
     * Транзакционный метод для тестирования вызова из него аннотированного транзакционного метода другого класса (который
     * будет транзакционным).
     * В этом методе происходит вызов транзакционного метода другого класса -
     * ТransactionAnnotatedServiceDefault.annotatedTransaction_REQUIRES_NEW().
     * Т.к. происходит вызов из другого класса, то аннотация @Transactional для annotatedTransaction_REQUIRES_NEW()
     * <b>срабатывает</b> и в методе выполнится commit () - вставленные им данные остаются в таблице AA_TRANS. Это
     * происходит не смотря на то, что в основной транзакции происходит ошибка (и следственно ее rollback).
     * </pre>
     */
    public AaTransRecord zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW(
            JooqTransactionAnnotatedServiceDefault jooqTransactionAnnotatedServiceDefault) {
        AaTransRecord result = transactionTemplateZyme02.execute(new TransactionCallback<AaTransRecord>() {
            @Override
            public AaTransRecord doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW [{}]: transId = {}", schema, record.getTransId());

                    ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord record_REQUIRES_NEW =
                            jooqTransactionAnnotatedServiceDefault.defaultJooqAnnotatedTransaction_REQUIRES_NEW();
                    throw new RuntimeException("Break zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW()");
                } catch (Throwable e) {
                    LOG.error("zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW [" + schema + "]: Run SQL Error");
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод не возвращающий значение и обновляющий данные в БД zyme02.
     * Используются новый TransactionTemplate (управляемый своим transactionManager) и свой dslContext (БД zyme02).
     */
    public void newZyme02TransactionTemplate_TransactionCallbackWithoutResult() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "newZyme02TransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("newZyme02TransactionTemplate_TransactionCallbackWithoutResult [{}]: transId = {}", schema, record.getTransId());
                } catch (Throwable e) {
                    LOG.error("newZyme02TransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme02.
     * Используются новый TransactionTemplate (управляемый своим transactionManager) и свой dslContext (БД zyme02).
     */
    public AaTransRecord newZyme02TransactionTemplate_TransactionCallback() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        AaTransRecord result = template.execute(new TransactionCallback<AaTransRecord>() {
            @Override
            public AaTransRecord doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "newZyme02TransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("newZyme02TransactionTemplate_TransactionCallback [{}]: transId = {}", schema, record.getTransId());
                    return record;
                } catch (Throwable e) {
                    LOG.error("newZyme02TransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

}
