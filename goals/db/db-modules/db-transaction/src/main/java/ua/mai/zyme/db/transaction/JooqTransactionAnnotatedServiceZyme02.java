package ua.mai.zyme.db.transaction;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa02.schema.tables.records.AaTransRecord;
import ua.mai.zyme.db.config.JdbcConfigurationZyme02Settings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa02.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme02</i>, в котором:
 *   - используется JOOQ со <b>своим глобальным</b> <i>dslContextZyme02</i> (БД <i>zyme02</i>).
 *   - транзакционность задается аннотациями, использующими <b>свой глобальный</b> <i>transactionManagerZyme02</i>.
 *
 * Методы:
 *     
 * Сервис для работы с БД <i>zyme02</i>.
 * Транзакционность задается <b>аннотациями</b>. Используются свой <i>transactionManagerZyme02</i> и свой <i>dslContextZyme02</i>
 * (БД <i>zyme02</i>).
 *
 *    <i>zyme02AnnotatedTransaction()</i> - аннотированный транзакционный метод возвращающий значение и обновляющий
 *      данные в БД <i>zyme02</i>. При вызове этого метода используется текущая транзакция. Если текущей нет, то
 *      открывается новая.
 *    <i>zyme02AnnotatedTransaction_REQUIRES_NEW()</i> - аннотированный транзакционный метод возвращающий значение и
 *      обновляющий данные в БД <i>zyme02</i>. При вызове этого метода открывается новая транзакция.
 *    <i>zyme02AnnotatedTransaction_callOwn_zyme02AnnotatedTransaction_REQUIRES_NEW()</i> - аннотированный транзакционный
 *      метод для тестирования вызова из него другого транзакциооного метода этого же класса, <b>который не будет
 *      транзакционным</b>. При вызове этого метода открывается/используется текущая транзакция.
 * </pre>
 */
@Service
@Transactional
public class JooqTransactionAnnotatedServiceZyme02 {

    private static final Logger LOG = LoggerFactory.getLogger(JooqTransactionAnnotatedServiceZyme02.class);

    //------------ Объекты настроены для zyme02 -------------------------------
    // Свой глобальный DSLContext (БД zyme02):
    private final DSLContext dslContextZyme02;
    //-----------------------------------------------------------------------

    private static final String schema = "zyme02";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JooqTransactionAnnotatedServiceZyme02(
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.DSL_CONTEXT_ZYME02) DSLContext dslContextZyme02) // свой DSLContext (для zyme02)
    {
        this.dslContextZyme02 = dslContextZyme02;
    }

    /**
     * <pre>
     * Аннотированный транзакционный метод возвращающий значение и обновляющий данные в БД <i>zyme02</i>.
     * При вызове этого метода используется текущая транзакция. Если текущей нет, то открывается новая.
     * Используется транзакция заданная аннотацией (управляемая своим transactionManager) и свой dslContext (БД zyme02).
     * </pre>
     */
    @Transactional(transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
    public AaTransRecord zyme02AnnotatedTransaction() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "zyme02AnnotatedTransaction [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("zyme02AnnotatedTransaction [{}]: transId = {}", schema, record.getTransId());
        return record;
    }

    /**
     * <pre>
     * Аннотированный транзакционный метод возвращающий значение и обновляющий данные в БД zyme02.
     * При вызове этого метода открывается новая транзакция.
     * Транзакция задается аннотацией c Propagation.REQUIRES_NEW (управляется своим transactionManager)
     * и своим dslContext (БД zyme02).
     * </pre>
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW, transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
    public AaTransRecord zyme02AnnotatedTransaction_REQUIRES_NEW() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "zyme02AnnotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("zyme02AnnotatedTransaction_REQUIRES_NEW [{}]: transId = {}", schema, record.getTransId());
        return record;
    }

    /**
     * <pre>
     * Аннотированный транзакционный метод для тестирования вызова из него другого транзакциооного метода этого же
     * класса, <b>который не будет транзакционным<b>.
     * При вызове этого метода открывается/используется текущая транзакция.
     * В этом методе происходит вызов транзакционного метода этого же класса - zyme02AnnotatedTransaction_REQUIRES_NEW(). Но т.к.
     * происходит вызов из этого же класса, то аннотация @Transactional для zyme02AnnotatedTransaction_REQUIRES_NEW()
     * <b>не срабатывает</b> - т.е. новая транзакция не создается! И в таблицу AA_TRANS не будет вставлено ни одной записи!
     * </pre>
     */
    @Transactional(transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
    public AaTransRecord zyme02AnnotatedTransaction_callOwn_zyme02AnnotatedTransaction_REQUIRES_NEW() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "zyme02AnnotatedTransaction_callOwn_zyme02AnnotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContextZyme02.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("zyme02AnnotatedTransaction_callOwn_zyme02AnnotatedTransaction_REQUIRES_NEW [{}]: transId = {}", schema, record.getTransId());
        AaTransRecord record_REQUIRES_NEW =  zyme02AnnotatedTransaction_REQUIRES_NEW();
        throw new RuntimeException("Break zyme02AnnotatedTransaction_callOwn_zyme02AnnotatedTransaction_REQUIRES_NEW() [" + schema + "] ");
    }

}
