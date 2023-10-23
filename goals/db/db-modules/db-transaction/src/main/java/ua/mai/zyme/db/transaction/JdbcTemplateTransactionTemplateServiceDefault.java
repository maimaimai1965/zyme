package ua.mai.zyme.db.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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

import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme</i>, в котором:
 *   - используются <b>дефолтный</b> <i>jdbcTemplate</i> или <b>новый</b> <i>JdbcTemplate</i> или <b>дефолтный</b>
 *     <i>namedParameterJdbcTemplate</i> или <b>новый</b> <i>NamedParameterJdbcTemplate</i>;
 *   - используется <b>дефолтный</b> <i>transactionManager</i>;
 *   - транзакционность обеспечивается <b>дефолтным</b> <i>transactionTemplate</i> или <b>новым</b> <i>TransactionTemplate</i>.
 *
 * Методы:
 *   <i>defaultJdbcTransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод <u>не возвращающий</u>
 *       значения и обновляющий данные в БД zyme. Используются <b>дефолтный</b> <i>transactionTemplate</i> (управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>) и <b>дефолтный</b> <i>jdbcTemplate</i>.
 *   <i>defaultJdbcTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий</u> значение и
 *       обновляющий данные в БД <i>zyme</i>. Используются <b>дефолтный</b> <i>transactionTemplate</i> (управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>) и <b>дефолтный</b> <i>jdbcTemplate</i>.
 *   <i>defaultJdbcNewTransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод <u>не возвращающий</u>
 *       значение и обновляющий данные в БД <i>zyme</i>. Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>) и <b>дефолтный</b> <i>jdbcTemplate</i>.
 *   <i>defaultJdbcNewTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий значение</u> и
 *       обновляющий данные в БД <i>zyme</i>. Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>) и <b>дефолтный</b> <i>jdbcTemplate</i>.
 *   <i>defaultNamedParameterJdbcTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий значение</u>
 *       и обновляющий данные в БД <i>zyme</i>. Используются <b>дефолтный</b> <i>transactionTemplate</i> (управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>) и <b>дефолтный</b> <i>namedParameterJdbcTemplate</i>.
 *   <i>defaultNamedParameterJdbcNewTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий значение</u> и
 *       обновляющий данные в БД <i>zyme</i>. Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>) и <b>дефолтный</b> <i>namedParameterJdbcTemplate</i>.
 * </pre>
 */
@Service
public class JdbcTemplateTransactionTemplateServiceDefault {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateTransactionTemplateServiceDefault.class);

    //------------ Объекты настроены для zyme (Default): --------------------
    // Дефолтный jdbcTemplate:
    private final JdbcTemplate jdbcTemplate;
    // Дефолтный namedParameterJdbcTemplate (для дефолтного transactionManager):
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    // Дефолтный transactionTemplate (для дефолтного transactionManager):
    private final TransactionTemplate transactionTemplate;
    // Дефолтный transactionManager (используется для создания новых TransactionTemplate)
    private final PlatformTransactionManager transactionManager;
    //-----------------------------------------------------------------------

    // Используется только для информирования. В настройках не используется.
    private static final String schema = "zyme";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JdbcTemplateTransactionTemplateServiceDefault(
                @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
                @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate, // дефолтный namedParameterJdbcTemplate (для zyme)
                @Autowired TransactionTemplate transactionTemplate,               // дефолтный transactionTemplate (для zyme)
                @Autowired PlatformTransactionManager transactionManager          // дефолтный PlatformTransactionManager (для zyme)
                ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.transactionManager = transactionManager;
    }

    /**
     * Транзакционный метод не возвращающий значения и обновляющий данные в БД zyme.
     * Используются дефолтный transactionTemplate (управляемый дефолтным transactionManager) и дефолтный jdbcTemplate.
     */
    public void defaultJdbcTransactionTemplate_TransactionCallbackWithoutResult() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultJdbcTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplate.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("defaultJdbcTransactionTemplate_TransactionCallbackWithoutResult [{}]", schema);
                } catch (Throwable e) {
                    LOG.error("defaultJdbcTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются дефолтный transactionTemplate (управляемый дефолтным transactionManager) и дефолтный jdbcTemplate.
     */
    public Integer defaultJdbcTransactionTemplate_TransactionCallback() {
         Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultJdbcTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplate.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("defaultJdbcTransactionTemplate_TransactionCallback [{}]: i={}", schema, i);
                    return i;
                } catch (Throwable e) {
                    LOG.error("defaultJdbcTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод не возвращающий значение и обновляющий данные в БД zyme.
     * Используются новый TransactionTemplate (управляемый дефолтным transactionManager) и дефолтный jdbcTemplate.
     */
    public void defaultJdbcNewTransactionTemplate_TransactionCallbackWithoutResult() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultJdbcNewTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplate.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("defaultJdbcNewTransactionTemplate_TransactionCallbackWithoutResult [{}]", schema);
                } catch (Throwable e) {
                    LOG.error("defaultJdbcNewTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются новый TransactionTemplate (управляемый дефолтным transactionManager) и дефолтный jdbcTemplate.
     */
    public Integer defaultJdbcNewTransactionTemplate_TransactionCallback() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Integer result = template.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultJdbcNewTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplate.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("defaultJdbcNewTransactionTemplate_TransactionCallback [{}]: i = {}", schema, i);
                    return i;
                } catch (Throwable e) {
                    LOG.error("defaultJdbcNewTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются дефолтный transactionTemplate (управляемый дефолтным transactionManager) и дефолтный namedParameterJdbcTemplate.
     */
    public Integer defaultNamedParameterJdbcTransactionTemplate_TransactionCallback() {
        Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultNamedParameterJdbcTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    SqlParameterSource namedParameters = new MapSqlParameterSource()
                            .addValue("p_trans_name", transName)
                            .addValue("p_created_dt", now);

                    int i = namedParameterJdbcTemplate.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                            namedParameters);

                    LOG.debug("defaultNamedParameterJdbcTransactionTemplate_TransactionCallback() [{}]: ", schema);
                    return i;
                } catch (Throwable e) {
                    LOG.error("defaultNamedParameterJdbcTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются новый TransactionTemplate (управляемый дефолтным transactionManager) и дефолтный namedParameterJdbcTemplate.
     */
    public Integer defaultNamedParameterJdbcNewTransactionTemplate_TransactionCallback() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Integer result = template.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "defaultNamedParameterJdbcNewTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    SqlParameterSource namedParameters = new MapSqlParameterSource()
                            .addValue("p_trans_name", transName)
                            .addValue("p_created_dt", now);

                    int i = namedParameterJdbcTemplate.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                            namedParameters);

                    LOG.debug("defaultNamedParameterJdbcNewTransactionTemplate_TransactionCallback() [{}]: ", schema);
                    return i;
                } catch (Throwable e) {
                    LOG.error("defaultNamedParameterJdbcNewTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

}
