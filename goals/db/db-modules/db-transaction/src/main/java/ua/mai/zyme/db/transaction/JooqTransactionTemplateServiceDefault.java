package ua.mai.zyme.db.transaction;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.*;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme</i>, в котором:
 *   - используется JOOQ с <b>дефолтным</b> <i>dslContext</i> (БД <i>zyme</i>);
 *   - используется <b>дефолтный</b> <i>transactionManager</i>;
 *   - транзакционность обеспечивается <b>дефолтным</b> <i>transactionTemplate</i> или <b>новым</b> <i>TransactionTemplate</i>.
 *
 * Методы:
 *   <i>defaultTransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод не возвращающий
 *       значения и обновляющий данные в БД <i>zyme</i>.
 *   <i>defaultTransactionTemplate_TransactionCallback()</i> - транзакционный метод возвращающий значение и обновляющий
 *       данные в БД <i>zyme</i>.
 *   <i>defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW()</i> - транзакционный метод для тестирования
 *       вызова из него аннотированного транзакционного метода другого класса (который <b>будет транзакционным</b>).
 *   <i>newTransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод не возвращающий значение и
 *       обновляющий данные в БД <i>zyme</i>, использующий <b>новый</b> TransactionTemplate.
 *   <i>newTransactionTemplate_TransactionCallback()</i> - транзакционный метод возвращающий значение и обновляющий данные в
 *       БД <i>zyme</i>, использующий <b>новый</b> TransactionTemplate.
 * </pre>
 */
@Service
public class JooqTransactionTemplateServiceDefault {

    private static final Logger LOG = LoggerFactory.getLogger(JooqTransactionTemplateServiceDefault.class);

    //------------ Объекты настроены для zyme (Default): --------------------
    // Дефолтный dslContext (БД zyme):
    private final DSLContext dslContext;
    // Дефолтный transactionTemplate (управляемый дефолтным transactionManager):
    private final TransactionTemplate transactionTemplate;
    // transactionManager используется для создания новых TransactionTemplate
    private final PlatformTransactionManager transactionManager;
    //-----------------------------------------------------------------------

    // Используется только для информирования. В настройках не используется.
    private static final String schema = "zyme";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JooqTransactionTemplateServiceDefault(
               @Autowired DSLContext dslContext,                         // дефолтный dslContext (для zyme)
               @Autowired TransactionTemplate transactionTemplate,       // дефолтный transactionTemplate (для zyme)
               @Autowired PlatformTransactionManager transactionManager  // дефолтный PlatformTransactionManager (для zyme)
              ) {
        this.dslContext = dslContext;
        this.transactionTemplate = transactionTemplate;
        this.transactionManager = transactionManager;
    }

    /**
     * Транзакционный метод не возвращающий значения и обновляющий данные в БД zyme.
     * Используются дефолтный transactionTemplate (управляемый дефолтным transactionManager) и дефолтный dslContext (БД zyme).
     */
    public void defaultTransactionTemplate_TransactionCallbackWithoutResult() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultTransactionTemplate [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContext.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("defaultTransactionTemplate [{}]: transId = {}", schema, record.getTransId());
                } catch (Throwable e) {
                    LOG.error("defaultTransactionTemplate [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются дефолтный transactionTemplate (управляемый дефолтным transactionManager) и дефолтный dslContext (БД zyme).
     */
    public AaTransRecord defaultTransactionTemplate_TransactionCallback() {
        AaTransRecord result = transactionTemplate.execute(new TransactionCallback<AaTransRecord>() {
            @Override
            public AaTransRecord doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContext.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("defaultTransactionTemplate_TransactionCallback [{}]: transId = {}", schema, record.getTransId());
                    return record;
                } catch (Throwable e) {
                    LOG.error("defaultTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод для тестирования вызова из него аннотированного транзакционного метода другого класса (который
     * будет транзакционным).
     * В этом методе происходит вызов транзакционного метода другого класса -ТransactionAnnotatedServiceDefault.annotatedTransaction_REQUIRES_NEW().
     * Т.к. происходит вызов из другого класса, то аннотация @Transactional для annotatedTransaction_REQUIRES_NEW()
     * <b>срабатывает</b> и в методе выполнится commit () - вставленные им данные остаются в таблице AA_TRANS. Это
     * происходит не смотря на то, что в основной транзакции происходит ошибка (и следственно ее rollback).
     */
    public AaTransRecord defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW(
                  JooqTransactionAnnotatedServiceDefault jooqTransactionAnnotatedServiceDefault) {
        AaTransRecord result = transactionTemplate.execute(new TransactionCallback<AaTransRecord>() {
            @Override
            public AaTransRecord doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContext.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW [{}]: transId = {}", schema, record.getTransId());

                    AaTransRecord record_REQUIRES_NEW =  jooqTransactionAnnotatedServiceDefault.defaultJooqAnnotatedTransaction_REQUIRES_NEW();
                    throw new RuntimeException("Break defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW()");
                } catch (Throwable e) {
                    LOG.error("defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW [" + schema + "]: Run SQL Error");
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод не возвращающий значение и обновляющий данные в БД zyme.
     * Используются новый TransactionTemplate (управляемый дефолтным transactionManager) и дефолтный dslContext (БД zyme).
     */
    public void newTransactionTemplate_TransactionCallbackWithoutResult() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "newTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContext.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("newTransactionTemplate_TransactionCallbackWithoutResult [{}]: transId = {}", schema, record.getTransId());
                } catch (Throwable e) {
                    LOG.error("newTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются новый TransactionTemplate (управляемый дефолтным transactionManager) и дефолтный dslContext (БД zyme)
     * для отдельной транзакции с возвратом данных.
     */
    public AaTransRecord newTransactionTemplate_TransactionCallback() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        AaTransRecord result = template.execute(new TransactionCallback<AaTransRecord>() {
            @Override
            public AaTransRecord doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "newTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    AaTransRecord record = dslContext.insertInto(AA_TRANS)
                            .set(AA_TRANS.TRANS_NAME, transName)
                            .set(AA_TRANS.CREATED_DT, now)
                            .returning()
                            .fetchOne();
                    LOG.debug("newTransactionTemplate_TransactionCallback [{}]: transId = {}", schema, record.getTransId());
                    return record;
                } catch (Throwable e) {
                    LOG.error("newTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

}
