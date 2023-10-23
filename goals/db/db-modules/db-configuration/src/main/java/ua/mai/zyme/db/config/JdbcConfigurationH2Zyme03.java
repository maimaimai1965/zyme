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
 * Бины для доступа к БД <i>zyme02</i> (для своих настроек).
 *
 * - бин <b>"zyme02.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationH2Zyme03$HikariConfigH2Zyme03"</b> - настройки
 *     <i>DataSource</i>, которые считываются из настроек Hikari пула соединений в <i>application.yml</i> в узле
 *     <i>zyme02.datasource.hikari</i>;
 * - бин <b>"hikariDatasourceH2Zyme03"</b> - объект <i>DataSource</i>. При его создании используется бин настроек <i>DataSource</i>.
 * - бин <b>"transactionManagerH2Zyme03"</b> - объект <i>PlatformTransactionManager</i>. При его создании используется бин
 *     <i>"hikariDatasourceH2Zyme03"</i>.
 * - бин <b>"transactionTemplateH2Zyme03"</b> - объект <i>TransactionTemplate</i>. При его создании используется бин
 *     <i>"transactionManagerH2Zyme03"</i>.
 * - бин <b>"settingsH2Zyme03"</b> - объект настроек JOOQ <i>Settings</i> для БД <i>zyme02</i>.
 * - бин <b>"dslContext"</b> - объекта <i>DSLContext</i>, который используется в JOOQ для доступа к БД <i>zyme</i>. При
 *     его создании используются бины <i>"transactionManagerH2Zyme03"</i>, <i>"hikariDatasourceH2Zyme03"</i> и
 *     <i>"settingsH2Zyme03"</i>.
 * </pre>
 */
@Configuration
@EnableConfigurationProperties({
        JdbcConfigurationH2Zyme03.HikariConfigH2Zyme03.class
})
public class JdbcConfigurationH2Zyme03 {

    @ConfigurationProperties(prefix="h2-zyme03.datasource.hikari")
    public static class HikariConfigH2Zyme03 extends HikariConfig {}

    @Bean(JdbcConfigurationH2Zyme03Settings.DATASOURCE_H2_ZYME03)
    public DataSource hikariDatasourceH2Zyme03(@Autowired HikariConfigH2Zyme03 config) {
        return new HikariDataSource(config);
    }

    @Bean(JdbcConfigurationH2Zyme03Settings.TRANSACTION_MANAGER_H2_ZYME03)
    public PlatformTransactionManager transactionTransactionManager(
            @Qualifier(JdbcConfigurationH2Zyme03Settings.DATASOURCE_H2_ZYME03) DataSource dataSource)
    {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean(JdbcConfigurationH2Zyme03Settings.TRANSACTION_TEMPLATE_H2_ZYME03)
    public TransactionTemplate transactionTemplate(
            @Qualifier(JdbcConfigurationH2Zyme03Settings.TRANSACTION_MANAGER_H2_ZYME03) PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean(JdbcConfigurationH2Zyme03Settings.JOOQ_SETTINGS_H2_ZYME03)
    public Settings jooqSettingsH2Zyme03(@Value("${h2-zyme03.db.schema}") String schema) {
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

    @Bean(JdbcConfigurationH2Zyme03Settings.DSL_CONTEXT_H2_ZYME03)
    public DSLContext dslContextH2Zyme03(
            @Autowired @Qualifier(JdbcConfigurationH2Zyme03Settings.TRANSACTION_MANAGER_H2_ZYME03) PlatformTransactionManager transactionManager,
            @Autowired @Qualifier(JdbcConfigurationH2Zyme03Settings.DATASOURCE_H2_ZYME03) DataSource dataSource,
//            @Autowired @Qualifier("jooqExecuteListenerProvider") ExecuteListenerProvider executeListenerProvider,
            @Autowired @Qualifier(JdbcConfigurationH2Zyme03Settings.JOOQ_SETTINGS_H2_ZYME03) Settings settings
    ) {
        return DSL.using(new DefaultConfiguration()
                .derive(settings)
                .derive(SQLDialect.MYSQL)
//                .derive(executeListenerProvider)
                .derive(new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource)))
                .derive(new SpringTransactionProvider(transactionManager))
        );
    }

    @Bean(JdbcConfigurationH2Zyme03Settings.JDBC_TEMPLATE_H2_ZYME03)
    public JdbcTemplate jdbcTemplateH2Zyme03(
            @Qualifier(JdbcConfigurationH2Zyme03Settings.DATASOURCE_H2_ZYME03) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(JdbcConfigurationH2Zyme03Settings.NAMED_PARAMETER_JDBC_TEMPLATE_H2_ZYME03)
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateH2Zyme03(
            @Qualifier(JdbcConfigurationH2Zyme03Settings.DATASOURCE_H2_ZYME03) DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
