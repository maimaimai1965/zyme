package ua.mai.zyme.db.config;

/**
 * Названия бинов, использующихся для доступа к БД <i>zyme</i> (дефолтные спринговые настройки).
 */
public interface JdbcConfigurationDefaultSettings {

    public static final String DATASOURCE_DEFAULT = "dataSource";
    public static final String TRANSACTION_MANAGER_DEFAULT = "transactionManager";
    public static final String TRANSACTION_TEMPLATE_DEFAULT = "transactionTemplate";
    public static final String JOOQ_SETTINGS_DEFAULT = "settings";
    public static final String DSL_CONTEXT_DEFAULT = "dslContext";
    public static final String JDBC_TEMPLATE_DEFAULT = "jdbcTemplate";
    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "namedParameterJdbcTemplate";

}
