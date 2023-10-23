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
 * Бины для доступа к БД MySQL (для дефолтных спринговых настроек, которые задаются в spring.*).
 *
 * - бин <b>"spring.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationDefault$HikariConfigDefault"</b> - настройки
 *     <i>DataSource</i>, которые считываются из настроек Hikari пула соединений в <i>application.yml</i> в узле
 *     <i>spring.datasource.hikari</i>;
 * - бин <b>"dataSource"</b> - объект <i>DataSource</i>. При его создании используется бин настроек <i>DataSource</i>.
 * - бин <b>"transactionManager"</b> - объект <i>PlatformTransactionManager</i>. При его создании используется бин <b>"dataSource"</b>.
 * - бин <b>"transactionTemplate"</b> - объект <i>TransactionTemplate</i>. При его создании используется бин <b>"transactionManager"</b>.
 * - бин <b>"settings"</b> - объект настроек JOOQ <i>Settings</i> для БД <i>zyme</i>.
 * - бин <b>"dslContext"</b> - объекта <i>DSLContext</i>, который используется в JOOQ для доступа к БД <i>zyme</i>. При
 *     его создании используются бины <b>"transactionManager"</b>, <b>"dataSource"</b> и <b>"settings"</b>.
 * </pre>
 */
@Configuration
@EnableConfigurationProperties({
        JdbcConfigurationDefault.HikariConfigDefault.class
})
public class JdbcConfigurationDefault {

    @ConfigurationProperties(prefix="spring.datasource.hikari")
    public static class HikariConfigDefault extends HikariConfig {}

    @Bean(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT)
    @Primary
    public DataSource hikariDatasourceDefault(@Autowired HikariConfigDefault config) {
        return new HikariDataSource(config);
    }

    @Bean(JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT)
    @Primary
    public PlatformTransactionManager transactionManagerDefault(@Autowired DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean(JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT)
    @Primary
    public TransactionTemplate transactionTemplateDefault(@Autowired PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }


    @Bean(JdbcConfigurationDefaultSettings.JOOQ_SETTINGS_DEFAULT)
    @Primary
    public Settings jooqSettings(@Value("${spring.db.schema}") String schema) {
        return new Settings()
                .withRenderMapping(new RenderMapping()
                        .withDefaultSchema(schema)
                        .withSchemata(
                                new MappedSchema().withInput("AA").withOutput(schema),
                                new MappedSchema().withInput("ZO").withOutput(schema)
//                                new MappedSchema().withInput("OAM").withOutput(schema),
//                                new MappedSchema().withInput("PGW").withOutput(schema)
                        ));
    }

//    @Bean
//    public ExceptionTransformer jooqExecuteListenerProvider() {
//        return new ExceptionTransformer();
//    }

    @Bean(JdbcConfigurationDefaultSettings.DSL_CONTEXT_DEFAULT)
    @Primary
    public DSLContext dslContext(
            @Autowired PlatformTransactionManager transactionManager,  // используется дефолтный спринговый PlatformTransactionManager
            @Autowired DataSource dataSource,
//            @Autowired @Qualifier("jooqExecuteListenerProvider") ExecuteListenerProvider executeListenerProvider,
            @Autowired Settings settings    // jooqSettings
    ) {
        return DSL.using(new DefaultConfiguration()
                .derive(settings)
                .derive(SQLDialect.MYSQL)
//                .derive(executeListenerProvider)
                .derive(new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource)))
                .derive(new SpringTransactionProvider(transactionManager))
        );
    }

    @Bean(JdbcConfigurationDefaultSettings.JDBC_TEMPLATE_DEFAULT)
    @Primary
    public JdbcTemplate jdbcTemplateDefault(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(JdbcConfigurationDefaultSettings.NAMED_PARAMETER_JDBC_TEMPLATE)
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateDefault(@Autowired DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
