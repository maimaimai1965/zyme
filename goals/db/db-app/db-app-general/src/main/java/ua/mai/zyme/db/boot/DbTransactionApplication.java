package ua.mai.zyme.db.boot;

import org.jooq.DSLContext;
import org.jooq.TransactionProvider;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;
import ua.mai.zyme.db.config.*;
import ua.mai.zyme.db.transaction.*;
import ua.mai.zyme.db.transaction.config.DbTransactionConfig;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

import javax.sql.DataSource;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
  (scanBasePackageClasses = {
        DbTransactionConfig.class
  }, exclude = {
//        JooqAutoConfiguration.class
//        SecurityAutoConfiguration.class,
//        ErrorMvcAutoConfiguration.class,
//        QuartzAutoConfiguration.class,
//        MailSenderAutoConfiguration.class
  })
@Import({
        JdbcConfigurationDefault.class,
        JdbcConfigurationZyme02.class,
})
@EnableConfigurationProperties
 ({
//    AaProperties.class,
//    MessageServiceProperties.class,
 })
//@EnableFeignClients(basePackages = {"ua.telesens.o320.pgw.fcs.clients"})
public class DbTransactionApplication {

    private static final Logger log = LoggerFactory.getLogger(DbTransactionApplication.class);

    private final Environment env;

    public DbTransactionApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
//        System.getProperties().setProperty("spring.application.name", "PGW Backend Services");
        SpringApplication app = new SpringApplication(DbTransactionApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            log.info("------------- zyme (Default) beans: --------------------------------------------------------------");
            JdbcConfigurationDefault.HikariConfigDefault configDefault =
                    (JdbcConfigurationDefault.HikariConfigDefault)ctx.getBean("spring.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationDefault$HikariConfigDefault");
            log.info("{}:  {}", "spring.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationDefault$HikariConfigDefault", configDefault);

            DataSource dataSource = (DataSource)ctx.getBean(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT, dataSource);

            TransactionManager transactionManager = (TransactionManager)ctx.getBean(JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT, transactionManager);

            TransactionTemplate transactionTemplate = (TransactionTemplate)ctx.getBean(JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT, transactionTemplate);

            Settings settings = (Settings)ctx.getBean(JdbcConfigurationDefaultSettings.JOOQ_SETTINGS_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.JOOQ_SETTINGS_DEFAULT, settings);

            DSLContext dslContext = (DSLContext)ctx.getBean(JdbcConfigurationDefaultSettings.DSL_CONTEXT_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.DSL_CONTEXT_DEFAULT, dslContext);

            JdbcTemplate jdbcTemplate = (JdbcTemplate)ctx.getBean(JdbcConfigurationDefaultSettings.JDBC_TEMPLATE_DEFAULT);
            log.info("jdbcTemplate:  {}", jdbcTemplate);

            NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                    (NamedParameterJdbcTemplate)ctx.getBean(JdbcConfigurationDefaultSettings.NAMED_PARAMETER_JDBC_TEMPLATE);
            log.info("namedParameterJdbcTemplate:  {}", namedParameterJdbcTemplate);

            TransactionProvider transactionProvider = (TransactionProvider)ctx.getBean("transactionProvider");
            log.info("transactionProvider:  {}", transactionProvider);

            TransactionInterceptor transactionInterceptor = (TransactionInterceptor)ctx.getBean("transactionInterceptor");
            log.info("transactionInterceptor:  {}", transactionInterceptor);

            // Сервисы:
            JooqTransactionAnnotatedServiceDefault jooqTransactionAnnotatedServiceDefault =
                    (JooqTransactionAnnotatedServiceDefault)ctx.getBean("jooqTransactionAnnotatedServiceDefault");
            log.info("transactionAnnotatedServiceDefault:  {}", jooqTransactionAnnotatedServiceDefault);

            JooqTransactionTemplateServiceDefault jooqTransactionTemplateServiceDefault =
                    (JooqTransactionTemplateServiceDefault)ctx.getBean("jooqTransactionTemplateServiceDefault");
            log.info("transactionTemplateServiceDefault:  {}", jooqTransactionTemplateServiceDefault);

            JdbcTemplateAnnotatedServiceDefault jdbcTemplateAnnotatedServiceDefault =
                    (JdbcTemplateAnnotatedServiceDefault)ctx.getBean("jdbcTemplateAnnotatedServiceDefault");
            log.info("jdbcTemplateServiceDefault:  {}", jdbcTemplateAnnotatedServiceDefault);

            JdbcTemplateTransactionTemplateServiceDefault jdbcTemplateTransactionTemplateServiceDefault =
                    (JdbcTemplateTransactionTemplateServiceDefault)ctx.getBean("jdbcTemplateTransactionTemplateServiceDefault");
            log.info("jdbcTemplateTransactionTemplateServiceDefault:  {}", jdbcTemplateTransactionTemplateServiceDefault);


            log.info("------------- zyme02 beans: --------------------------------------------------------------------");
            JdbcConfigurationZyme02.HikariConfigZyme02 hikariDatasourceZyme02 =
                    (JdbcConfigurationZyme02.HikariConfigZyme02)ctx.getBean("zyme02.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationZyme02$HikariConfigZyme02");
            log.info("{}:  {}", "zyme02.datasource.hikari-ua.mai.zyme.db.config.JdbcConfigurationZyme02$HikariConfigZyme02", hikariDatasourceZyme02);

            DataSource dataSourceZyme02 = (DataSource)ctx.getBean(JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02);
            log.info("{}:  {}", JdbcConfigurationZyme02Settings.DATASOURCE_ZYME02, dataSourceZyme02);

            TransactionManager transactionManagerZyme02 = (TransactionManager)ctx.getBean(JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02);
            log.info("{}:  {}", JdbcConfigurationZyme02Settings.TRANSACTION_MANAGER_ZYME02, transactionManagerZyme02);

            TransactionTemplate transactionTemplateZyme02 = (TransactionTemplate)ctx.getBean(JdbcConfigurationZyme02Settings.TRANSACTION_TEMPLATE_ZYME02);
            log.info("{}:  {}", JdbcConfigurationZyme02Settings.TRANSACTION_TEMPLATE_ZYME02, transactionTemplateZyme02);

            Settings settingsZyme02 = (Settings)ctx.getBean(JdbcConfigurationZyme02Settings.JOOQ_SETTINGS_ZYME02);
            log.info("{}:  {}", JdbcConfigurationZyme02Settings.JOOQ_SETTINGS_ZYME02, settingsZyme02);

            DSLContext dslContextZyme02 = (DSLContext)ctx.getBean(JdbcConfigurationZyme02Settings.DSL_CONTEXT_ZYME02);
            log.info("{}:  {}", JdbcConfigurationZyme02Settings.DSL_CONTEXT_ZYME02, dslContextZyme02);

            JdbcTemplate jdbcTemplateZyme02 = (JdbcTemplate)ctx.getBean(JdbcConfigurationZyme02Settings.JDBC_TEMPLATE_ZYME02);
            log.info("{}:  {}", JdbcConfigurationZyme02Settings.JDBC_TEMPLATE_ZYME02, jdbcTemplateZyme02);

            NamedParameterJdbcTemplate namedParameterJdbcTemplateZyme02 =
                    (NamedParameterJdbcTemplate)ctx.getBean(JdbcConfigurationZyme02Settings.NAMED_PARAMETER_JDBC_TEMPLATE_ZYME02);
            log.info("namedParameterJdbcTemplateZyme02:  {}", namedParameterJdbcTemplateZyme02);

            // Сервисы:
            JooqTransactionAnnotatedServiceZyme02 jooqTransactionAnnotatedServiceZyme02 = (JooqTransactionAnnotatedServiceZyme02)ctx.getBean("jooqTransactionAnnotatedServiceZyme02");
            log.info("transactionAnnotatedServiceZyme02:  {}", jooqTransactionAnnotatedServiceZyme02);

            JooqTransactionTemplateServiceZyme02 jooqTransactionTemplateServiceZyme02 = (JooqTransactionTemplateServiceZyme02)ctx.getBean("jooqTransactionTemplateServiceZyme02");
            log.info("transactionTemplateServiceZyme02:  {}", jooqTransactionTemplateServiceZyme02);

            JdbcTemplateAnnotatedServiceZyme02 jdbcTemplateAnnotatedServiceZyme02 = (JdbcTemplateAnnotatedServiceZyme02)ctx.getBean("jdbcTemplateAnnotatedServiceZyme02");
            log.info("jdbcTemplateAnnotatedServiceZyme02:  {}", jdbcTemplateAnnotatedServiceZyme02);

            JdbcTemplateTransactionTemplateServiceZyme02 jdbcTemplateTransactionTemplateServiceZyme02 = (JdbcTemplateTransactionTemplateServiceZyme02)ctx.getBean("jdbcTemplateTransactionTemplateServiceZyme02");
            log.info("jdbcTemplateTransactionTemplateServiceZyme02:  {}", jdbcTemplateTransactionTemplateServiceZyme02);


jdbcTemplateTransactionTemplateServiceZyme02.zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback();

            log.info("------------- zyme (Default) methods calls: --------------------------------------------------------------");
            AaTransRecord record;

            // transactionAnnotatedServiceDefault:
            record = jooqTransactionAnnotatedServiceDefault.defaultJooqAnnotatedTransaction();
            record = jooqTransactionAnnotatedServiceDefault.defaultJooqAnnotatedTransaction_REQUIRES_NEW();
            try {
                record = jooqTransactionAnnotatedServiceDefault.defaultJooqAnnotatedTransaction_callOwn_annotatedTransaction_REQUIRES_NEW();
            } catch (Exception e) {
            }
            record = jooqTransactionAnnotatedServiceDefault.noJooqAnnotatedTransactionMethod_in_AnnotatedTransactionClass();

            // transactionTemplateServiceDefault:
            jooqTransactionTemplateServiceDefault.defaultTransactionTemplate_TransactionCallbackWithoutResult();
            record = jooqTransactionTemplateServiceDefault.defaultTransactionTemplate_TransactionCallback();
            try {
                record = jooqTransactionTemplateServiceDefault.defaultTransactionTemplate_call_annotatedTransaction_REQUIRES_NEW(jooqTransactionAnnotatedServiceDefault);
            } catch (Exception e) {
            }
            jooqTransactionTemplateServiceDefault.newTransactionTemplate_TransactionCallbackWithoutResult();
            record = jooqTransactionTemplateServiceDefault.newTransactionTemplate_TransactionCallback();

            // jdbcTemplateAnnotatedServiceDefault:
            int i = jdbcTemplateAnnotatedServiceDefault.defaultJdbcTemplateAnnotatedDefaultTransaction();
            i = jdbcTemplateAnnotatedServiceDefault.newJdbcTemplateAnnotatedDefaultTransaction_REQUIRES_NEW();
            i = jdbcTemplateAnnotatedServiceDefault.defaultNamedParameterJdbcTemplateAnnotatedDefaultTransaction();
            i = jdbcTemplateAnnotatedServiceDefault.newNamedParameterJdbcTemplateAnnotatedDefaultTransaction();

            // jdbcTemplateTransactionTemplateServiceDefault
            jdbcTemplateTransactionTemplateServiceDefault.defaultJdbcTransactionTemplate_TransactionCallbackWithoutResult();
            i = jdbcTemplateTransactionTemplateServiceDefault.defaultJdbcTransactionTemplate_TransactionCallback();
            jdbcTemplateTransactionTemplateServiceDefault.defaultJdbcNewTransactionTemplate_TransactionCallbackWithoutResult();
            i = jdbcTemplateTransactionTemplateServiceDefault.defaultJdbcNewTransactionTemplate_TransactionCallback();
            i = jdbcTemplateTransactionTemplateServiceDefault.defaultNamedParameterJdbcTransactionTemplate_TransactionCallback();
            i = jdbcTemplateTransactionTemplateServiceDefault.defaultNamedParameterJdbcNewTransactionTemplate_TransactionCallback();

            log.info("------------- zyme02 methods calls: ----------------------------------------------------------------------");
            ua.mai.zyme.db.aa02.schema.tables.records.AaTransRecord record02;

            // transactionAnnotatedServiceZyme02:
            record02 = jooqTransactionAnnotatedServiceZyme02.zyme02AnnotatedTransaction();
            record02 = jooqTransactionAnnotatedServiceZyme02.zyme02AnnotatedTransaction_REQUIRES_NEW();
            try {
                record02 = jooqTransactionAnnotatedServiceZyme02.zyme02AnnotatedTransaction_callOwn_zyme02AnnotatedTransaction_REQUIRES_NEW();
            } catch (Exception e) {
            }

            // transactionTemplateServiceZyme02:
            jooqTransactionTemplateServiceZyme02.zyme02TransactionTemplate_TransactionCallbackWithoutResult();
            record02 = jooqTransactionTemplateServiceZyme02.zyme02TransactionTemplate_TransactionCallback();
            try {
                record02 = jooqTransactionTemplateServiceZyme02.zyme02TransactionTemplate_call_annotatedTransaction_REQUIRES_NEW(jooqTransactionAnnotatedServiceDefault);
            } catch (Exception e) {
            }
            jooqTransactionTemplateServiceZyme02.newZyme02TransactionTemplate_TransactionCallbackWithoutResult();
            record02 = jooqTransactionTemplateServiceZyme02.newZyme02TransactionTemplate_TransactionCallback();

            // jdbcTemplateAnnotatedServiceZyme02:
            i = jdbcTemplateAnnotatedServiceZyme02.zyme02JdbcTemplateAnnotatedTransaction();
            i = jdbcTemplateAnnotatedServiceZyme02.newZyme02JdbcTemplateAnnotatedTransaction_REQUIRES_NEW();
            i = jdbcTemplateAnnotatedServiceZyme02.zyme02NamedParameterJdbcTemplateAnnotatedDefaultTransaction();
            i = jdbcTemplateAnnotatedServiceZyme02.newZyme02NamedParameterJdbcTemplateAnnotatedTransaction_REQUIRES_NEW();

            // jdbcTemplateTransactionTemplateServiceZyme02
            jdbcTemplateTransactionTemplateServiceZyme02.zyme02JdbcTransactionTemplate_TransactionCallbackWithoutResult();
            i = jdbcTemplateTransactionTemplateServiceZyme02.zyme02JdbcTransactionTemplate_TransactionCallback();
            jdbcTemplateTransactionTemplateServiceZyme02.zyme02JdbcNewTransactionTemplate_TransactionCallbackWithoutResult();
            i = jdbcTemplateTransactionTemplateServiceZyme02.zyme02JdbcNewTransactionTemplate_TransactionCallback();
            i = jdbcTemplateTransactionTemplateServiceZyme02.zyme02NamedParameterJdbcTransactionTemplate_TransactionCallback();
            i = jdbcTemplateTransactionTemplateServiceZyme02.zyme02NamedParameterJdbcNewTransactionTemplate_TransactionCallback();

//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
        };
    }

}

