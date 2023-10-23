package ua.mai.zyme.db.boot;

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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.config.DbConfig;
import ua.mai.zyme.db.config.JdbcConfigurationDefault;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultSettings;
import ua.mai.zyme.db.config.JdbcConfigurationZyme02;
import ua.mai.zyme.db.jdbc.*;
import ua.mai.zyme.db.jdbc.config.DbJdbcTemplateConfig;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
  (scanBasePackageClasses = {
        DbJdbcTemplateConfig.class
  }, exclude = {
//        JooqAutoConfiguration.class
  })
@Import({
        JdbcConfigurationDefault.class,
        JdbcConfigurationZyme02.class,
})
@EnableConfigurationProperties
 ({
//    AaProperties.class
 })
public class DbJdbcTemplateApplication {

    private static final Logger log = LoggerFactory.getLogger(DbJdbcTemplateApplication.class);

    private final Environment env;

    public DbJdbcTemplateApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
//        System.getProperties().setProperty("spring.application.name", "PGW Backend Services");
        SpringApplication app = new SpringApplication(DbJdbcTemplateApplication.class);
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

            NamedParameterJdbcTemplate namedParameterJdbcTemplate = (NamedParameterJdbcTemplate)ctx.getBean("namedParameterJdbcTemplate");
            log.info("namedParameterJdbcTemplate:  {}", namedParameterJdbcTemplate);

            JdbcTemplateInsertSqlService jdbcTemplateInsertSqlService = (JdbcTemplateInsertSqlService)ctx.getBean("jdbcTemplateInsertSqlService");
            log.info("jdbcTemplateInsertSqlService:  {}", jdbcTemplateInsertSqlService);

            JdbcTemplateUpdateSqlService jdbcTemplateUpdateSqlService = (JdbcTemplateUpdateSqlService)ctx.getBean("jdbcTemplateUpdateSqlService");
            log.info("jdbcTemplateUpdateSqlService:  {}", jdbcTemplateUpdateSqlService);

            JdbcTemplateDeleteSqlService jdbcTemplateDeleteSqlService = (JdbcTemplateDeleteSqlService)ctx.getBean("jdbcTemplateDeleteSqlService");
            log.info("jdbcTemplateDeleteSqlService:  {}", jdbcTemplateDeleteSqlService);

            JdbcTemplateSelectSqlService jdbcTemplateSelectSqlService = (JdbcTemplateSelectSqlService)ctx.getBean("jdbcTemplateSelectSqlService");
            log.info("jdbcTemplateSelectSqlService:  {}", jdbcTemplateSelectSqlService);

            JdbcTemplateInsertBatchSqlService jdbcTemplateInsertBatchSqlService = (JdbcTemplateInsertBatchSqlService)ctx.getBean("jdbcTemplateInsertBatchSqlService");
            log.info("jdbcTemplateInsertBatchSqlService:  {}", jdbcTemplateInsertBatchSqlService);

            int i;

jdbcTemplateInsertBatchSqlService.insertBatchListWithNamedParameter("insertBatchListWithNamedParameter()", "Batch 7 records:", LocalDateTime.now(), 7);

            log.info("------------- jdbcTemplateInsertSqlService methods calls: ----------------------------------------");
            jdbcTemplateInsertSqlService.insertOneNoReturn("insertOneNoReturn()", null, LocalDateTime.now());

            log.info("------------- jdbcTemplateUpdateSqlService methods calls: ----------------------------------------");
            jdbcTemplateUpdateSqlService.update(1L, "jdbcTemplateUpdateSqlService", null,  LocalDateTime.now());

            log.info("------------- jdbcTemplateDeleteSqlService methods calls: ----------------------------------------");
            jdbcTemplateDeleteSqlService.delete();

            log.info("------------- jdbcTemplateSelectSqlService methods calls: ----------------------------------------");
            jdbcTemplateSelectSqlService.selectOneColumnOneRow();
            jdbcTemplateSelectSqlService.selectOneRow();
            jdbcTemplateSelectSqlService.selectManyRows();

            log.info("------------- jdbcTemplateInsertBatchSqlService methods calls: -----------------------------------");
            jdbcTemplateInsertBatchSqlService.insertBatchList("insertBatchList()", "Batch 11 records:", LocalDateTime.now(), 11);
            jdbcTemplateInsertBatchSqlService.insertBatchListWithNamedParameter("insertBatchListWithNamedParameter()", "Batch 7 records:", LocalDateTime.now(), 7);

//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
        };
    }

}

