package ua.mai.zyme.db.transaction;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme</i>, в котором:
 *   - используется JOOQ с <b>дефолтным</b> <i>dslContext</i> (БД <i>zyme</i>);
 *   - транзакционность задается аннотациями, использующими <b>дефолтный</b> <i>transactionManager</i>.
 *
 * Методы:
 *   <i>defaultJooqAnnotatedTransaction()</i> - транзакционный метод (заданный аннотацией) возвращающий значение и
 *      обновляющий данные в БД <i>zyme</i> c помощью JOOQ (<b>дефолтного</b> <i>dslContext</i>) и управляемый
 *      <b>дефолтным</b> <i>transactionManager</i>. Используется текущая транзакция, а если текущей нет, то открывается новая.
 *   <i>defaultJooqAnnotatedTransaction_REQUIRES_NEW()</i> - транзакционный метод (заданный аннотацией c
 *      <i>Propagation.REQUIRES_NEW</i>) возвращающий значение и обновляющий данные в БД <i>zyme</i> c помощью JOOQ
 *      (<b>дефолтного</b> <i>dslContext</i>) и управляемый <b>дефолтным</b> <i>transactionManager</i>. При
 *      вызове этого метода открывается новая транзакция.
 *   <i>defaultJooqAnnotatedTransaction_callOwn_annotatedTransaction_REQUIRES_NEW()</i> - транзакционный метод (заданный
 *      аннотацией) для тестирования вызова из него другого транзакциооного метода этого же класса (который <b>не будет
 *      </b> вызываться в новой транзакции, хотя он определен как REQUIRES_NEW).
 *   <i>noJooqAnnotatedTransactionMethod_in_AnnotatedTransactionClass()</i> - <b>не аннотированный</b> транзакционный метод
 *      в аннотированном транзакционном классе возвращающий значение и <b>обновляющий</b> данные в БД. Не смотря на то,
 *      что аннотации <i>@Transactional</i> у метода нет, этот метод является транзакционным (т.к. это <i>public</i>
 *      метод и у класса есть аннотация <i>@Transactional</i>).
 * </pre>
 */
@Service
@Transactional
public class JooqTransactionAnnotatedServiceDefault {

    private static final Logger LOG = LoggerFactory.getLogger(JooqTransactionAnnotatedServiceDefault.class);

    //------------ Объекты настроены для zyme (Default): --------------------
    // Дефолтный dslContext (БД zyme) zyme:
    private final DSLContext dslContext;
    //-----------------------------------------------------------------------

    private static final String schema = "zyme";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JooqTransactionAnnotatedServiceDefault(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    /**
     * Транзакционный метод (заданный аннотацией) возвращающий значение и обновляющий данные в БД zyme c помощью JOOQ
     * (дефолтного dslContext) и управляемый дефолтным transactionManager. Используется текущая транзакция, а если
     * текущей нет, то открывается новая.
     */
    @Transactional
    public AaTransRecord defaultJooqAnnotatedTransaction() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "defaultJooqAnnotatedTransaction [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContext.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("defaultJooqAnnotatedTransaction [{}]: transId = {}", schema, record.getTransId());
        return record;
    }

    /**
     * Транзакционный метод (заданный аннотацией c Propagation.REQUIRES_NEW) возвращающий значение и обновляющий данные
     * в БД zyme c помощью JOOQ (дефолтного dslContext) и управляемый дефолтным transactionManager. При вызове этого
     * метода открывается новая транзакция.
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public AaTransRecord defaultJooqAnnotatedTransaction_REQUIRES_NEW() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "defaultJooqAnnotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContext.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("defaultJooqAnnotatedTransaction_REQUIRES_NEW [{}]: transId = {}", schema, record.getTransId());
        return record;
    }

    /**
     * Транзакционный метод (заданный аннотацией) для тестирования вызова из него другого транзакциооного метода этого же
     * класса (который не будет транзакционным).
     * При вызове этого метода открывается/используется текущая транзакция.
     * В этом методе происходит вызов транзакционного метода этого же класса - annotatedTransaction_REQUIRES_NEW(). Но т.к.
     * происходит вызов из этого же класса, то аннотация @Transactional для annotatedTransaction_REQUIRES_NEW()
     * <b>не срабатывает</b> - т.е. новая транзакция не создается! Он выполняется в той же транзакции, в которой
     * происходит ошибка. В результате в таблицу AA_TRANS не будет вставлено ни одной записи!
     */
    @Transactional
    public AaTransRecord defaultJooqAnnotatedTransaction_callOwn_annotatedTransaction_REQUIRES_NEW() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "defaultJooqAnnotatedTransaction_callOwn_annotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContext.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("defaultJooqAnnotatedTransaction_callOwn_annotatedTransaction_REQUIRES_NEW [{}]: transId = {}", schema, record.getTransId());
        AaTransRecord record_REQUIRES_NEW =  defaultJooqAnnotatedTransaction_REQUIRES_NEW();
        throw new RuntimeException("Break defaultAnnotatedTransaction_callOwn_annotatedTransaction_REQUIRES_NEW() [" + schema + "] ");
    }

    /**
     * Не аннотированный транзакционный метод в аннотированном транзакционном классе возвращающий значение и обновляющий
     * данные в БД zyme. Не смотря на то, что аннотации @Transactional у метода нет, этот метод является транзакционным
     * (т.к. это public метод и у класса есть аннотация @Transactional).
     * При вызове этого метода используется текущая транзакция. Если текущей нет, то открывается новая.
     * Используются транзакция заданная аннотацией (управляемая дефолтным transactionManager) и
     * дефолтный dslContext (БД zyme).
     */
    public AaTransRecord noJooqAnnotatedTransactionMethod_in_AnnotatedTransactionClass() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "noJooqAnnotatedTransactionMethod_in_AnnotatedTransactionClass [" + schema + "] " + now.format(formatter);
        AaTransRecord record = dslContext.insertInto(AA_TRANS)
                .set(AA_TRANS.TRANS_NAME, transName)
                .set(AA_TRANS.CREATED_DT, now)
                .returning()
                .fetchOne();
        LOG.debug("noJooqAnnotatedTransactionMethod_in_AnnotatedTransactionClass [{}]: transId = {}", schema, record.getTransId());
        return record;
    }

}
