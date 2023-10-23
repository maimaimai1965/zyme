package ua.mai.zyme.db.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * <pre>
 * Бины для доступа к БД <i>zyme</i> (для своих настроек).
 *
 * - бин <b>"zyme.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationZyme$HikariConfigZyme"</b> - настройки
 *     <i>DataSource</i>, которые считываются из настроек Hikari пула соединений в <i>application.yml</i> в узле
 *     <i>zyme.datasource.hikari</i>;
 * - бин <b>"hikariDatasourceZyme"</b> - объект <i>DataSource</i>. При его создании используется бин настроек <i>DataSource</i>.
 * - бин <b>"transactionManagerZyme"</b> - объект <i>PlatformTransactionManager</i>. При его создании используется бин
 *     <i>"hikariDatasourceZyme"</i>.
 * - бин <b>"transactionTemplateZyme"</b> - объект <i>TransactionTemplate</i>. При его создании используется бин
 *     <i>"transactionManagerZyme"</i>.
 * - бин <b>"settingsZyme"</b> - объект настроек JOOQ <i>Settings</i> для БД <i>zyme</i>.
 * - бин <b>"dslContext"</b> - объекта <i>DSLContext</i>, который используется в JOOQ для доступа к БД <i>zyme</i>. При
 *     его создании используются бины <i>"transactionManagerZyme"</i>, <i>"hikariDatasourceZyme"</i> и
 *     <i>"settingsZyme"</i>.
 * </pre>
 */
@Configuration
@EnableConfigurationProperties({
        JdbcConfigurationZyme.HikariConfigZyme.class
})
public class JdbcConfigurationZyme {

    @ConfigurationProperties(prefix="zyme.datasource.hikari")
    public static class HikariConfigZyme extends HikariConfig {}

    @Bean(JdbcConfigurationZymeSettings.DATASOURCE_ZYME02)
    public DataSource hikariDatasourceZyme(@Autowired HikariConfigZyme config) {
        return new HikariDataSource(config);
    }

    @Bean(JdbcConfigurationZymeSettings.TRANSACTION_MANAGER_ZYME02)
    public PlatformTransactionManager transactionTransactionManager(
            @Qualifier(JdbcConfigurationZymeSettings.DATASOURCE_ZYME02) DataSource dataSource)
    {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean(JdbcConfigurationZymeSettings.TRANSACTION_TEMPLATE_ZYME02)
    public TransactionTemplate transactionTemplate(
            @Qualifier(JdbcConfigurationZymeSettings.TRANSACTION_MANAGER_ZYME02) PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean(JdbcConfigurationZymeSettings.JOOQ_SETTINGS_ZYME02)
    public Settings jooqSettingsZyme(@Value("${zyme.db.schema}") String schema) {
        return new Settings()
                .withRenderMapping(new RenderMapping()
                        .withDefaultSchema(schema)
                        .withSchemata(
                                new MappedSchema().withInput("AA").withOutput(schema)
//                                new MappedSchema().withInput("MR").withOutput(schema),
//                                new MappedSchema().withInput("OAM").withOutput(schema),
//                                new MappedSchema().withInput("PGW").withOutput(schema)
                        ));
    }

//    @Bean
//    public ExceptionTransformer jooqExecuteListenerProvider() {
//        return new ExceptionTransformer();
//    }

    @Bean(JdbcConfigurationZymeSettings.DSL_CONTEXT_ZYME02)
    public DSLContext dslContextZyme(
            @Autowired @Qualifier(JdbcConfigurationZymeSettings.TRANSACTION_MANAGER_ZYME02) PlatformTransactionManager transactionManager,
            @Autowired @Qualifier(JdbcConfigurationZymeSettings.DATASOURCE_ZYME02) DataSource dataSource,
//            @Autowired @Qualifier("jooqExecuteListenerProvider") ExecuteListenerProvider executeListenerProvider,
            @Autowired @Qualifier(JdbcConfigurationZymeSettings.JOOQ_SETTINGS_ZYME02) Settings settings
    ) {
        return DSL.using(new DefaultConfiguration()
                .derive(settings)
                .derive(SQLDialect.MYSQL)
//                .derive(executeListenerProvider)
                .derive(new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource)))
                .derive(new SpringTransactionProvider(transactionManager))
        );
    }

    @Bean(JdbcConfigurationZymeSettings.JDBC_TEMPLATE_ZYME02)
    public JdbcTemplate jdbcTemplateZyme(
            @Qualifier(JdbcConfigurationZymeSettings.DATASOURCE_ZYME02) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(JdbcConfigurationZymeSettings.NAMED_PARAMETER_JDBC_TEMPLATE_ZYME02)
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme(
            @Qualifier(JdbcConfigurationZymeSettings.DATASOURCE_ZYME02) DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
