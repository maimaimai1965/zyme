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
import org.springframework.context.annotation.Primary;
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
 * - бин <b>"zyme02.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationZyme02$HikariConfigZyme02"</b> - настройки
 *     <i>DataSource</i>, которые считываются из настроек Hikari пула соединений в <i>application.yml</i> в узле
 *     <i>zyme02.datasource.hikari</i>;
 * - бин <b>"hikariDatasourceZyme02"</b> - объект <i>DataSource</i>. При его создании используется бин настроек <i>DataSource</i>.
 * - бин <b>"transactionManagerZyme02"</b> - объект <i>PlatformTransactionManager</i>. При его создании используется бин
 *     <i>"hikariDatasourceZyme02"</i>.
 * - бин <b>"transactionTemplateZyme02"</b> - объект <i>TransactionTemplate</i>. При его создании используется бин
 *     <i>"transactionManagerZyme02"</i>.
 * - бин <b>"settingsZyme02"</b> - объект настроек JOOQ <i>Settings</i> для БД <i>zyme02</i>.
 * - бин <b>"dslContext"</b> - объекта <i>DSLContext</i>, который используется в JOOQ для доступа к БД <i>zyme</i>. При
 *     его создании используются бины <i>"transactionManagerZyme02"</i>, <i>"hikariDatasourceZyme02"</i> и
 *     <i>"settingsZyme02"</i>.
 * </pre>
 */
@Configuration
@EnableConfigurationProperties({
        JdbcConfigurationZyme02.HikariConfigZyme02.class
})
public class JdbcConfigurationZyme02 {

    @ConfigurationProperties(prefix="zyme02.datasource.hikari")
    public static class HikariConfigZyme02 extends HikariConfig {}

    @Bean(JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02)
    public DataSource hikariDatasourceZyme02(@Autowired HikariConfigZyme02 config) {
        return new HikariDataSource(config);
    }

    @Bean(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02)
    public PlatformTransactionManager transactionTransactionManager(
            @Qualifier(JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02) DataSource dataSource)
    {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean(JdbcConfigurationZyme02Settings.TRANSACTION_TEMPLATE_ZYME02)
    public TransactionTemplate transactionTemplate(
            @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02) PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean(JdbcConfigurationZyme02Settings.JOOQ_SETTINGS_ZYME02)
    public Settings jooqSettingsZyme02(@Value("${zyme02.db.schema}") String schema) {
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

    @Bean(JdbcConfigurationZyme02Settings.DSL_CONTEXT_ZYME02)
    public DSLContext dslContextZyme02(
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02) PlatformTransactionManager transactionManager,
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02) DataSource dataSource,
//            @Autowired @Qualifier("jooqExecuteListenerProvider") ExecuteListenerProvider executeListenerProvider,
            @Autowired @Qualifier(JdbcConfigurationZyme02Settings.JOOQ_SETTINGS_ZYME02) Settings settings
    ) {
        return DSL.using(new DefaultConfiguration()
                .derive(settings)
                .derive(SQLDialect.MYSQL)
//                .derive(executeListenerProvider)
                .derive(new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource)))
                .derive(new SpringTransactionProvider(transactionManager))
        );
    }

    @Bean(JdbcConfigurationZyme02Settings.JDBC_TEMPLATE_ZYME02)
    public JdbcTemplate jdbcTemplateZyme02(
            @Qualifier(JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(JdbcConfigurationZyme02Settings.NAMED_PARAMETER_JDBC_TEMPLATE_ZYME02)
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme02(
            @Qualifier(JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02) DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
