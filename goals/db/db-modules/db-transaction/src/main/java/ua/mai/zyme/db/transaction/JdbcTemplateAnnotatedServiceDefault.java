package ua.mai.zyme.db.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Сервис для работы с БД <i>zyme</i>, в котором:
 *   - используются <b>дефолтный</b> <i>jdbcTemplate</i> или <b>новый</b> <i>JdbcTemplate</i> или
 *      <b>дефолтный</b> <i>namedParameterJdbcTemplate</i> или <b>новый</b> <i>NamedParameterJdbcTemplate</i>;
 *   - транзакционность задается аннотациями, использующими <b>дефолтный</b> <i>transactionManager</i>.
 *
 * Методы:
 *   <i>defaultJdbcTemplateAnnotatedDefaultTransaction()</i> - транзакционный метод (заданный аннотацией), обновляющий
 *       данные в БД <i>zyme</i> с помощью <b>дефолтного</b> <i>jdbcTemplate</i> и управляемый <b>дефолтным</b>
 *       <i>transactionManager.</i> Используется текущая транзакция, а если текущей нет, то открывается новая.
 *   <i>newJdbcTemplateAnnotatedDefaultTransaction_REQUIRES_NEW()</i> - транзакционный метод  (заданный аннотацией),
 *       обновляющий данные в БД <i>zyme</i> с помощью <b>нового</b> <i>JdbcTemplate</i> и управляемый <b>дефолтным</b>
 *       <i>transactionManager</i>. Используется новая транзакция.
 *   <i>defaultNamedParameterJdbcTemplateAnnotatedDefaultTransaction()</i> - транзакционный метод (заданный аннотацией),
 *       обновляющий данные в БД <i>zyme</i> с помощью <b>дефолтного</b> <i>namedParameterJdbcTemplate</i> и управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>. Используется текущая транзакция, а если текущей нет, то открывается
 *       новая.
 *   <i>newNamedParameterJdbcTemplateAnnotatedDefaultTransaction()</i> - транзакционный метод (заданный аннотацией),
 *       обновляющий данные в БД <i>zyme</i> с помощью <b>нового</b> <i>NamedParameterJdbcTemplate</i> и управляемый
 *       <b>дефолтным</b> <i>transactionManager</i>. Используется текущая транзакция, а если текущей нет, то открывается
 *       новая.
 * </pre>
 */
@Service
public class JdbcTemplateAnnotatedServiceDefault {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTemplateAnnotatedServiceDefault.class);

    //------------ Объекты настроены для zyme (Default): --------------------
    // Дефолтный jdbcTemplate (для дефолтного transactionManager):
    private final JdbcTemplate jdbcTemplate;
    // Дефолтный namedParameterJdbcTemplate (для дефолтного transactionManager):
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // transactionManager используется для создания новых JdbcTemplate или новых NamedParameterJdbcTemplate:
    private final PlatformTransactionManager transactionManager;
    //-----------------------------------------------------------------------

    // Используется только для информирования. В настройках не используется.
    private static final String schema = "zyme";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public JdbcTemplateAnnotatedServiceDefault(
                 @Autowired JdbcTemplate jdbcTemplate,                             // дефолтный jdbcTemplate (для zyme)
                 @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate, // дефолтный namedParameterJdbcTemplate (для zyme)
                 @Autowired PlatformTransactionManager transactionManager          // дефолтный PlatformTransactionManager (для zyme)
                ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = transactionManager;
    }

    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme с помощью дефолтного JdbcTemplate и
     * управляемый дефолтным transactionManager. Используется текущая транзакция, а если текущей нет, то открывается
     * новая.
     */
    @Transactional
    public int defaultJdbcTemplateAnnotatedDefaultTransaction() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "defaultJdbcTemplateAnnotatedDefaultTransaction [" + schema + "] " + now.format(formatter);
        int i = jdbcTemplate.update(
                     "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                     transName, now);

        LOG.debug("defaultJdbcTemplateAnnotatedDefaultTransaction() [{}]: ", schema);
        return i;
    }

    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme с помощью нового JdbcTemplate,
     * управляемым дефолтным transactionManager. Используется новая транзакция.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int newJdbcTemplateAnnotatedDefaultTransaction_REQUIRES_NEW() {
        JdbcTemplate template = new JdbcTemplate(((JdbcTransactionManager)transactionManager).getDataSource());

        LocalDateTime now = LocalDateTime.now();
        String transName = "newJdbcTemplateAnnotatedTransaction_REQUIRES_NEW [" + schema + "] " + now.format(formatter);
        int i = template.update(
                "INSERT INTO aa_trans (trans_name, created_dt) VALUES (?, ?)",
                transName, now);

        LOG.debug("newJdbcTemplateAnnotatedTransaction_REQUIRES_NEW() [{}]: ", schema);
        return i;
    }

    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme с помощью дефолтного namedParameterJdbcTemplate
     * и управляемый дефолтным transactionManager. Используется текущая транзакция, а если текущей нет, то открывается
     * новая.
     */
    @Transactional
    public int defaultNamedParameterJdbcTemplateAnnotatedDefaultTransaction() {
        LocalDateTime now = LocalDateTime.now();
        String transName = "defaultNamedParameterJdbcTemplateAnnotatedDefaultTransaction [" + schema + "] " + now.format(formatter);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_trans_name", transName)
                .addValue("p_created_dt", now);

        int i = namedParameterJdbcTemplate.update(
                      "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                       namedParameters);

        LOG.debug("defaultNamedParameterJdbcTemplateAnnotatedDefaultTransaction() [{}]: ", schema);
        return i;
    }

    /**
     * Транзакционный метод (заданный аннотацией) обновляющий данные в БД zyme с помощью нового NamedParameterJdbcTemplate
     * и управляемый дефолтным transactionManager. Используется текущая транзакция, а если текущей нет, то открывается
     * новая.
     */
    @Transactional
    public int newNamedParameterJdbcTemplateAnnotatedDefaultTransaction() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(((JdbcTransactionManager)transactionManager).getDataSource());
        LocalDateTime now = LocalDateTime.now();
        String transName = "newNamedParameterJdbcTemplateAnnotatedDefaultTransaction [" + schema + "] " + now.format(formatter);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("p_trans_name", transName)
                .addValue("p_created_dt", now);

        int i = namedParameterJdbcTemplate.update(
                "INSERT INTO aa_trans (trans_name, created_dt) VALUES (:p_trans_name, :p_created_dt)",
                namedParameters);

        LOG.debug("newNamedParameterJdbcTemplateAnnotatedDefaultTransaction() [{}]: ", schema);
        return i;
    }

}
