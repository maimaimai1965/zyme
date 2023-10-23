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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;
import ua.mai.zyme.db.config.DbConfig;
import ua.mai.zyme.db.config.JdbcConfigurationDefault;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultSettings;
import ua.mai.zyme.db.config.JdbcConfigurationZyme02;
import ua.mai.zyme.db.jooq.*;
import ua.mai.zyme.db.jooq.config.DbJooqConfig;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
  (scanBasePackageClasses = {
        DbJooqConfig.class
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
//    AaProperties.class
 })
public class DbJooqApplication {

    private static final Logger log = LoggerFactory.getLogger(DbJooqApplication.class);

    private final Environment env;

    public DbJooqApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
//        System.getProperties().setProperty("spring.application.name", "PGW Backend Services");
        SpringApplication app = new SpringApplication(DbJooqApplication.class);
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
            DataSource dataSource = (DataSource)ctx.getBean(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT, dataSource);

            TransactionManager transactionManager = (TransactionManager)ctx.getBean(JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT, transactionManager);

            TransactionTemplate transactionTemplate = (TransactionTemplate)ctx.getBean(JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT, transactionTemplate);

            JdbcTemplate jdbcTemplate = (JdbcTemplate)ctx.getBean("jdbcTemplate");
            log.info("jdbcTemplate:  {}", jdbcTemplate);

            Settings settings = (Settings)ctx.getBean(JdbcConfigurationDefaultSettings.JOOQ_SETTINGS_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.JOOQ_SETTINGS_DEFAULT, settings);

            DSLContext dslContext = (DSLContext)ctx.getBean(JdbcConfigurationDefaultSettings.DSL_CONTEXT_DEFAULT);
            log.info("{}:  {}", JdbcConfigurationDefaultSettings.DSL_CONTEXT_DEFAULT, dslContext);

            TransactionProvider transactionProvider = (TransactionProvider)ctx.getBean("transactionProvider");
            log.info("transactionProvider:  {}", transactionProvider);

            TransactionInterceptor transactionInterceptor = (TransactionInterceptor)ctx.getBean("transactionInterceptor");
            log.info("transactionInterceptor:  {}", transactionInterceptor);

            JooqSelectSqlService jooqSelectSqlService = (JooqSelectSqlService)ctx.getBean("jooqSelectSqlService");
            log.info("jooqSelectSqlService:  {}", jooqSelectSqlService);

            JooqSelectNativeSqlService jooqSelectNativeSqlService = (JooqSelectNativeSqlService)ctx.getBean("jooqSelectNativeSqlService");
            log.info("jooqSelectNativeSqlService:  {}", jooqSelectNativeSqlService);

            JooqSelectCursorSqlService jooqSelectCursorSqlService = (JooqSelectCursorSqlService)ctx.getBean("jooqSelectCursorSqlService");
            log.info("jooqSelectCursorSqlService:  {}", jooqSelectCursorSqlService);

            JooqInsertSqlService jooqInsertSqlService = (JooqInsertSqlService)ctx.getBean("jooqInsertSqlService");
            log.info("jooqInsertSqlService:  {}", jooqInsertSqlService);

            JooqInsertBatchSqlService jooqInsertBatchSqlService = (JooqInsertBatchSqlService)ctx.getBean("jooqInsertBatchSqlService");
            log.info("jooqInsertBatchSqlService:  {}", jooqInsertBatchSqlService);

            JooqUpdateSqlService jooqUpdateSqlService = (JooqUpdateSqlService)ctx.getBean("jooqUpdateSqlService");
            log.info("jooqUpdateSqlService:  {}", jooqUpdateSqlService);

            JooqInsertBulkSqlService jooqInsertBulkSqlService = (JooqInsertBulkSqlService)ctx.getBean("jooqInsertBulkSqlService");
            log.info("jooqInsertBulkSqlService:  {}", jooqInsertBulkSqlService);

            JooqDeleteSqlService jooqDeleteSqlService = (JooqDeleteSqlService)ctx.getBean("jooqDeleteSqlService");
            log.info("jooqDeleteSqlService:  {}", jooqDeleteSqlService);

            JooqCopyService jooqCopyService = (JooqCopyService)ctx.getBean("jooqCopyService");
            log.info("jooqCopyService:  {}", jooqCopyService);

            JooqConvertersService jooqConvertersService = (JooqConvertersService)ctx.getBean("jooqConvertersService");
            log.info("jooqConvertersService:  {}", jooqConvertersService);

            AaTransRecord record;
            int count;

jooqSelectSqlService.selectWithWith();

            log.info("------------- jooqSelectSqlService methods calls: --------------------------------------------------------------");
            jooqSelectSqlService.selectFetchOne();
            jooqSelectSqlService.selectFetchOptional();
            jooqSelectSqlService.selectFetchInTableRecord();
            jooqSelectSqlService.selectFetchInAnotherClassWithCommonFields();
            List<String> list = jooqSelectSqlService.selectFetchValuesInList();
            jooqSelectSqlService.selectComplicatedSql();
            jooqSelectSqlService.selectWithAsterisk();
            jooqSelectSqlService.selectWithLimitOffset();
            jooqSelectSqlService.selectWithInField();
            jooqSelectSqlService.selectWithCompare();
            jooqSelectSqlService.selectWithCast();
            jooqSelectSqlService.selectWithCoerce();
            jooqSelectSqlService.selectWithSubqueryField();
            jooqSelectSqlService.selectFromSameTable();
            jooqSelectSqlService.selectDistinct();
            jooqSelectSqlService.selectWithWith();

            log.info("------------- jooqSelectNativeSqlService methods calls: --------------------------------------------------------------");
            jooqSelectNativeSqlService.selectNativeSqlIntoList();
            jooqSelectNativeSqlService.selectNativeSqlIntoListFetchLazy();

            log.info("------------- jooqSelectCursorSqlService methods calls: --------------------------------------------------------");
            jooqSelectCursorSqlService.selectFetchLazy();

            log.info("------------- jooqInsertSqlService methods calls: --------------------------------------------------------------");
            jooqInsertSqlService.insertOneNoReturn("insertNoReturn()", null, LocalDateTime.now());
            record = jooqInsertSqlService.insertOneWithReturnRecord("insertWithReturnRecord()", null, LocalDateTime.now());
            Long id = jooqInsertSqlService.insertOneWithReturnId("insertOneWithReturnId()", null, LocalDateTime.now());
            record = jooqInsertSqlService.insertOneWithReturnRecordVariant2("insertWithReturnRecordVariant2()", null, LocalDateTime.now());
            jooqInsertSqlService.insertOneWithNewAndStore("insertWithNewAndStore()", null, LocalDateTime.now());
            count = jooqInsertSqlService.insertRecordsFromSelect(4);
            jooqInsertSqlService.insertRecordsWithFor("insertRecords()", "Batch 11:", LocalDateTime.now(), 11);

            log.info("------------- jooqInsertBatchSqlService methods calls: --------------------------------------------------------------");
            jooqInsertBatchSqlService.insertRecordsWithBatchUsingRecordList("insertRecordsWithBatchUsingRecordList()", "Batch 15 records:", LocalDateTime.now(), 15);
            jooqInsertBatchSqlService.insertRecordsWithBatchDefiningNeededFields("insertRecordsWithBatchDefiningNeededFields()", "Batch 17 records:", LocalDateTime.now(), 17);
            jooqInsertBatchSqlService.insertRecordsWithBatchDefiningNeededFieldsVariant2("insertRecordsWithBatchDefiningNeededFieldsVariant2()", "Batch 19 records:", LocalDateTime.now(), 19);
            jooqInsertBatchSqlService.insertRecordsWithBatchThrowException();

            log.info("------------- jooqInsertBulkSqlService methods calls: --------------------------------------------------------------");
            jooqInsertBulkSqlService.insertRecordListWithBulk("insertRecordListWithBulk()", "Bulk 11 records:", LocalDateTime.now(), 11);
            jooqInsertBulkSqlService.insertRecordsWithBulkThrowSqlException();

            log.info("------------- jooqUpdateSqlService methods calls: --------------------------------------------------------------");
            jooqUpdateSqlService.update(1L, "jooqUpdateSqlService", null,  LocalDateTime.now());
            jooqUpdateSqlService.updateWithForUpdateAndStore(1234L, "updateWithForUpdateAndStore", null,  LocalDateTime.now());

            log.info("------------- jooqDeleteSqlService methods calls: --------------------------------------------------------------");
            count = jooqDeleteSqlService.delete();
            count = jooqDeleteSqlService.deleteWithRowIn();


            log.info("------------- jooqCopyService methods calls: -------------------------------------------------------------------");
            jooqCopyService.copySelectAndInsertWithSize(61, 20);

            log.info("------------- jooqConvertersService methods calls: -------------------------------------------------------------");
            jooqConvertersService.selectWithConverterForYearMonthType();
            jooqConvertersService.selectWithConverterForEnum();
            jooqConvertersService.insertWithConverterForYearMonthType();
            jooqConvertersService.insertWithConverterForEnum();
            jooqConvertersService.selectForcedEnum();
            jooqConvertersService.selectForcedEnumFieldAsChar();

//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
        };
    }

}

