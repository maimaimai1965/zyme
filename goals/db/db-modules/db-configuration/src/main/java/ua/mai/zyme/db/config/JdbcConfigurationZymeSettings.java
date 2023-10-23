package ua.mai.zyme.db.config;

/**
 * Названия бинов, использующихся для доступа к БД <i>zyme</i> (свои настройки).
 */
public interface JdbcConfigurationZymeSettings {

    public static final String DATASOURCE_ZYME02 = "hikariDatasourceZyme";
    public static final String TRANSACTION_MANAGER_ZYME02 = "transactionManagerZyme";
    public static final String TRANSACTION_TEMPLATE_ZYME02 = "transactionTemplateZyme";
    public static final String JOOQ_SETTINGS_ZYME02 = "settingsZyme";
    public static final String DSL_CONTEXT_ZYME02 = "dslContextZyme";
    public static final String JDBC_TEMPLATE_ZYME02 = "jdbcTemplateZyme";
    public static final String NAMED_PARAMETER_JDBC_TEMPLATE_ZYME02 = "namedParameterJdbcTemplateZyme";

}
