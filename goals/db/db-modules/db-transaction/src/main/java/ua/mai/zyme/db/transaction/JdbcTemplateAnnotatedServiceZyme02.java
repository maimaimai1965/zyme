package ua.mai.zyme.db.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.config.JdbcConfigurationZyme02Settings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme02</i>, в котором:
 *   - используются <b>свой глобальный</b> <i>jdbcTemplateZyme02</i> или <b>новый</b> <i>JdbcTemplate</i>;
 *   - транзакционность задается аннотациями, использующими свой <b>глобальный</b> <i>transactionTemplateZyme02</i>.
 *
 * Методы:
 *   <i>zyme02JdbcTemplateAnnotatedTransaction()</i> - транзакционный метод (заданный аннотацией) обновляющий
 *       данные в БД <i>zyme02</i> с помощью <b>своего глобального</b> <i>jdbcTemplateZyme02</i> и управляемый <b>своим глобальным</b>
 *       <i>transactionManagerZyme02</i>. Используется текущая транзакция, а если текущей нет, то открывается новая.
 *   <i>newZyme02JdbcTemplateAnnotatedTransaction_REQUIRES_NEW()</i> - транзакционный метод  (заданный аннотацией)
 *       обновляющий данные в БД <i>zyme02</i> с помощью <b>своего нового</b> JdbcTemplate, управляемым <b>своим глобальным</b>
 *       <i>transactionManagerZyme02</i>. Используется новая транзакция.
 *   <i>zyme02NamedParameterJdbcTemplateAnnotatedDefaultTransaction()</i> - транзакционный метод (заданный аннотацией)
 *       обновляющий данные в БД <i>zyme</i> с помощью <b>своего глобального</b> <i>namedParameterJdbcTemplate</i> и
 *       управляемый <b>своим глобальным</b> <i>transactionManagerZyme02</i>. Используется текущая транзакция, а если
 *       текущей нет, то открывается новая.
 *   <i>newZyme02NamedParameterJdbcTemplateAnnotatedTransaction_REQUIRES_NEW()</i> - транзакционный метод (заданный аннотацией)
 *       обновляющий данные в БД <i>zyme02</i> с помощью <b>нового</b> <i>NamedParameterJdbcTemplate</i>, управляемым
 *       <b>своим</b> <i>transactionManagerZyme02</i>. Используется новая транзакция.
 * </pre>
 */
@Service
public class JdbcTemplateAnnotatedServiceZyme02 {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateAnnotatedServiceZyme02.class);

    //------------ Объекты настроены для zyme02 --------------------
    // Свой JdbcTemplate (для своего transactionManagerZyme02):
    private final JdbcTemplate jdbcTemplateZyme02;
    // Свой глобальный namedParameterJdbcTemplate (для дефолтного transactionManager):
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme02;

    // Свой TransactionManager используется для создания новых JdbcTemplate и NamedParameterJdbcTemplate:
    private final PlatformTransactionManager transactionManagerZyme02;
    //-----------------------------------------------------------------------

    // Используется только для информирования. В настройках не используется.
    private static final String schema = "zyme02";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JdbcTemplateAnnotatedServiceZyme02(
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.JDBC_TEMPLATE_ZYME02)
            JdbcTemplate jdbcTemplateZyme02,                                                             // свой JdbcTemplate (для zyme02)
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.NAMED_PARAMETER_JDBC_TEMPLATE_ZYME02)
            NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme02,                                 // свой NamedParameterJdbcTemplate (для zyme02)
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
            PlatformTransactionManager transactionManagerZyme02                                          // свой PlatformTransactionManager (для zyme02)
           ) {
        this.jdbcTemplateZyme02 = jdbcTemplateZyme02;
        this.namedParameterJdbcTemplateZyme02 = namedParameterJdbcTemplateZyme02;
        this.transactionManagerZyme02 = transactionManagerZyme02;
    }

    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme02 с помощью своего глобального
     * jdbcTemplateZyme02 и управляемый своим transactionManagerZyme02. Используется текущая транзакция, а если текущей
     * нет, то открывается новая.
     */
    @Transactional(transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
    public int zyme02JdbcTemplateAnnotatedTransaction() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "zyme02JdbcTemplateAnnotatedTransaction [" + schema + "] " + now.format(formatter);
        int i = jdbcTemplateZyme02.update(
                     "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                     transName, now);
        LOG.debug("zyme02JdbcTemplateAnnotatedTransaction() [{}]: ", schema);
        return i;
    }


    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme02 с помощью своего нового JdbcTemplate,
     * управляемым своим transactionManagerZyme02. Используется новая транзакция.
     */
    @Transactional(transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02, propagation = Propagation.REQUIRES_NEW)
    public int newZyme02JdbcTemplateAnnotatedTransaction_REQUIRES_NEW() {
        JdbcTemplate template = new JdbcTemplate(((JdbcTransactionManager) transactionManagerZyme02).getDataSource());

        LocalDateTime now = LocalDateTime.now();
        String transName = "newZyme02JdbcTemplateAnnotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        int i = template.update(
                "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                transName, now);
        LOG.debug("newZyme02JdbcTemplateAnnotatedTransaction_REQUIRES_NEW() [{}]: ", schema);
        return i;
    }

    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme с помощью своего namedParameterJdbcTemplate
     * и управляемый своим transactionManagerZyme02. Используется текущая транзакция, а если текущей нет, то открывается
     * новая.
     */
    @Transactional(transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
    public int zyme02NamedParameterJdbcTemplateAnnotatedDefaultTransaction() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "zyme02NamedParameterJdbcTemplateAnnotatedDefaultTransaction [" + schema + "] " + now.format(formatter);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_trans_name", transName)
                .addValue("p_created_dt", now);

        int i = namedParameterJdbcTemplateZyme02.update(
                      "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                           namedParameters);

        LOG.debug("defaultNamedParameterJdbcTemplateAnnotatedDefaultTransaction() [{}]: ", schema);
        return i;
    }


    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme02 с помощью своего нового NamedParameterJdbcTemplate,
     * управляемым своим transactionManagerZyme02. Используется новая транзакция.
     */
    @Transactional(transactionManager = JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02, propagation = Propagation.REQUIRES_NEW)
    public int newZyme02NamedParameterJdbcTemplateAnnotatedTransaction_REQUIRES_NEW() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(((JdbcTransactionManager) transactionManagerZyme02).getDataSource());

        LocalDateTime now = LocalDateTime.now();
        String transName = "newZyme02NamedParameterJdbcTemplateAnnotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_trans_name", transName)
                .addValue("p_created_dt", now);

        int i = namedParameterJdbcTemplateZyme02.update(
                "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                namedParameters);

        LOG.debug("newZyme02NamedParameterJdbcTemplateAnnotatedTransaction_REQUIRES_NEW() [{}]: ", schema);
        return i;
    }

}
