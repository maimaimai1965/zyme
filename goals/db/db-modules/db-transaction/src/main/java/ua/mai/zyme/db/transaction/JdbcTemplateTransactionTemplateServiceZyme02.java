package ua.mai.zyme.db.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ua.mai.zyme.db.config.JdbcConfigurationZyme02Settings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme02</i>, в котором:
 *   - используются <b>свой</b> <i>jdbcTemplateZyme02</i> или <b>новый</b> <i>JdbcTemplate</i> или <b>дефолтный</b>
 *       <i>namedParameterJdbcTemplateZyme02</i> или <b>новый</b> <i>NamedParameterJdbcTemplate</i>;
 *   - используется <b>свой</b> <i>transactionManagerZyme02</i>;
 *   - транзакционность обеспечивается <b>своим</b> <i>transactionTemplateZyme02</i> или <b>новым</b>
 *       <i>TransactionTemplate</i>.
 *
 * Методы:
 *   <i>zyme02JdbcTransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод <u>не возвращающий</u>
 *       значения и обновляющий данные в БД <i>zyme02</i>. Используются <b>свой</b> <i>transactionTemplateZyme02</i>
 *       (управляемый <b>своим</b> <i>transactionManagerZyme02</i>) и <b>свой</b> <i>jdbcTemplateZyme02</i>.
 *   <i>zyme02JdbcTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий</u> значение и
 *       обновляющий данные в БД <i>zyme02</i>. Используются <b>свой</b> <i>transactionTemplateZyme02</i> (управляемый
 *       <b>своим</b> <i>transactionManagerZyme02</i>) и <b>свой</b> <i>jdbcTemplateZyme02</i>.
 *   <i>zyme02JdbcNewTransactionTemplate_TransactionCallbackWithoutResult()</i> - транзакционный метод <u>не возвращающий</u>
 *       значение и обновляющий данные в БД <i>zyme02</i>. Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый
 *       <b>своим</b> <i>transactionManagerZyme02</i>) и <b>свой</b> <i>jdbcTemplateZyme02</i>.
 *   <i>zyme02JdbcNewTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий значение</u> и
 *       обновляющий данные в БД <i>zyme02</i>. Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый
 *       <b>своим</b> <i>transactionManagerZyme02</i>) и <b>свой</b> <i>jdbcTemplateZyme02</i>.
 *   <i>zyme02NamedParameterJdbcTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий</u>
 *       значение и обновляющий данные в БД <i>zyme02</i>. Используются <b>свой</b> <i>transactionTemplateZyme02</i>
 *       (управляемый <b>своим</b> <i>transactionManagerZyme02</i>) и <b>свой</b> <i>namedParameterJdbcTemplateZyme02</i>.
 *   <i>zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback()</i> - транзакционный метод <u>возвращающий значение</u> и
 *       обновляющий данные в БД <i>zyme02</i>. Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый
 *       <b>своим</b> <i>transactionManagerZyme02</i>) и <b>свой</b> <i>namedParameterJdbcTemplateZyme02</i>.
 * </pre>
 */
@Service
public class JdbcTemplateTransactionTemplateServiceZyme02 {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateTransactionTemplateServiceZyme02.class);

    //------------ Объекты настроены для zyme02: ----------------------------
    // Свой jdbcTemplateZyme02:
    private final JdbcTemplate jdbcTemplateZyme02;
    // Свой namedParameterJdbcTemplateZyme02 (для дефолтного transactionManager):
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme02;
    // Свой transactionTemplateZyme02:
    private final TransactionTemplate transactionTemplateZyme02;
    // transactionManagerZyme02 используется для создания новых TransactionTemplate
    private final PlatformTransactionManager transactionManagerZyme02;
    //-----------------------------------------------------------------------

    // Используется только для информирования. В настройках не используется.
    private static final String schema = "zyme02";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JdbcTemplateTransactionTemplateServiceZyme02(
                 @Autowired @Qualifier(JdbcConfigurationZyme02Settings.JDBC_TEMPLATE_ZYME02)
                 JdbcTemplate jdbcTemplateZyme02,                                        // свой JdbcTemplate для zyme02
                 @Autowired @Qualifier(JdbcConfigurationZyme02Settings.NAMED_PARAMETER_JDBC_TEMPLATE_ZYME02)
                 NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme02,            // свой NamedParameterJdbcTemplate (для zyme)
                 @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_TEMPLATE_ZYME02)
                 TransactionTemplate transactionTemplateZyme02,                          // свой TransactionTemplate для zyme02
                 @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
                 PlatformTransactionManager transactionManagerZyme02                     // свой PlatformTransactionManager для zyme02
                ) {
        this.jdbcTemplateZyme02 = jdbcTemplateZyme02;
        this.namedParameterJdbcTemplateZyme02 = namedParameterJdbcTemplateZyme02;
        this.transactionTemplateZyme02 = transactionTemplateZyme02;
        this.transactionManagerZyme02 = transactionManagerZyme02;
    }

    /**
     * Транзакционный метод не возвращающий значения и обновляющий данные в БД zyme02.
     * Используются <b>свой</b> <i>transactionTemplateZyme02</i> (управляемый <b>своим</b>
     * <i>transactionManagerZyme02</i>) и <b>свой</b> <i>jdbcTemplateZyme02</i>.
     */
    public void zyme02JdbcTransactionTemplate_TransactionCallbackWithoutResult() {
        transactionTemplateZyme02.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02JdbcTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplateZyme02.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("zyme02JdbcTransactionTemplate_TransactionCallbackWithoutResult [{}]", schema);
                } catch (Throwable e) {
                    LOG.error("zyme02JdbcTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются <b>свой</b> <i>transactionTemplateZyme02</i> (управляемый <b>своим</b> <i>transactionManagerZyme02</i>)
     * и <b>свой</b> <i>jdbcTemplateZyme02</i>.
     */
    public Integer zyme02JdbcTransactionTemplate_TransactionCallback() {
         Integer result = transactionTemplateZyme02.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02JdbcTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplateZyme02.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("zyme02JdbcTransactionTemplate_TransactionCallback [{}]: i={}", schema, i);
                    return i;
                } catch (Throwable e) {
                    LOG.error("zyme02JdbcTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод не возвращающий значение и обновляющий данные в БД zyme.
     *  Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый <b>своим</b> <i>transactionManagerZyme02</i>)
     *  и <b>свой</b> <i>jdbcTemplateZyme02</i>.
     */
    public void zyme02JdbcNewTransactionTemplate_TransactionCallbackWithoutResult() {
        TransactionTemplate template = new TransactionTemplate(transactionManagerZyme02);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus paramTransactionStatus) {
                try {

                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02JdbcNewTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplateZyme02.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("zyme02JdbcNewTransactionTemplate_TransactionCallbackWithoutResult [{}]", schema);
                } catch (Throwable e) {
                    LOG.error("zyme02JdbcNewTransactionTemplate_TransactionCallbackWithoutResult [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                }
            }
        });
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый <b>своим</b> <i>transactionManagerZyme02</i>)
     * и <b>свой</b> <i>jdbcTemplateZyme02</i>.
     */
    public Integer zyme02JdbcNewTransactionTemplate_TransactionCallback() {
        TransactionTemplate template = new TransactionTemplate(transactionManagerZyme02);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Integer result = template.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02JdbcNewTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    int i = jdbcTemplateZyme02.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                            transName, now);
                    LOG.debug("zyme02JdbcNewTransactionTemplate_TransactionCallback [{}]: i = {}", schema, i);
                    return i;
                } catch (Throwable e) {
                    LOG.error("zyme02JdbcNewTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются <b>свой</b> <i>transactionTemplateZyme02</i> (управляемый <b>своим</b> <i>transactionManagerZyme02</i>)
     * и <b>свой</b> <i>namedParameterJdbcTemplateZyme02</i>.
     */
    public Integer zyme02NamedParameterJdbcTransactionTemplate_TransactionCallback() {
        Integer result = transactionTemplateZyme02.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02NamedParameterJdbcTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    SqlParameterSource namedParameters = new MapSqlParameterSource()
                            .addValue("p_trans_name", transName)
                            .addValue("p_created_dt", now);

                    int i = namedParameterJdbcTemplateZyme02.update(
                                "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                                     namedParameters);

                    LOG.debug("zyme02NamedParameterJdbcTransactionTemplate_TransactionCallback() [{}]: ", schema);
                    return i;
                } catch (Throwable e) {
                    LOG.error("zyme02NamedParameterJdbcTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

    /**
     * Транзакционный метод возвращающий значение и обновляющий данные в БД zyme.
     * Используются <b>новый</b> <i>TransactionTemplate</i> (управляемый <b>своим</b> <i>transactionManagerZyme02</i>)
     * и <b>свой</b> <i>namedParameterJdbcTemplateZyme02</i>.
     */
    public Integer zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback() {
        TransactionTemplate template = new TransactionTemplate(transactionManagerZyme02);
        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        Integer result = template.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus paramTransactionStatus) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    String transName = "zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback [" + schema + "] " + now.format(formatter);
                    SqlParameterSource namedParameters = new MapSqlParameterSource()
                            .addValue("p_trans_name", transName)
                            .addValue("p_created_dt", now);

                    int i = namedParameterJdbcTemplateZyme02.update(
                            "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                            namedParameters);

                    LOG.debug("zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback() [{}]: ", schema);
                    return i;
                } catch (Throwable e) {
                    LOG.error("zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback [" + schema + "]: Run SQL Error", e);
                    paramTransactionStatus.setRollbackOnly();
                    return null;
                }
            }
        });
        return result;
    }

}
