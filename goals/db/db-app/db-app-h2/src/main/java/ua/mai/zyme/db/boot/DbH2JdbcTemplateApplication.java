package ua.mai.zyme.db.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.transaction.support.TransactionTemplate;
import ua.mai.zyme.db.config.*;
import ua.mai.zyme.db.jdbc.*;
import ua.mai.zyme.db.jdbc.config.DbJdbcTemplateConfig;
import ua.mai.zyme.db.security.adapters.H2WebSecurityConfigurerAdapter;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

import javax.sql.DataSource;
import java.time.LocalDateTime;

/**
 * <pre>
 * Приложение:
 *   - использует embedded H2 DB (zyme03 - как дефолтную спринговую).
 * Просмотреть эту DB (когда выполняется приложение) можно через консоль - http://localhost:8102/h2-console.
 *   - использовать MySQL DB (zyme02 - со своими настроенными бинами).
 *   - предлагает различные варианты использования DbJdbcTemplate.
 * </pre>
 */
@SpringBootApplication
  (scanBasePackageClasses = {
        DbJdbcTemplateConfig.class
  }, exclude = {
//        JooqAutoConfiguration.class
  })
@Import({
        JdbcConfigurationDefaultH2.class,
        JdbcConfigurationZyme.class,
        H2WebSecurityConfigurerAdapter.class
})
@EnableConfigurationProperties
 ({
//    AaProperties.class
 })
public class DbH2JdbcTemplateApplication {

    private static final Logger log = LoggerFactory.getLogger(DbH2JdbcTemplateApplication.class);

    private final Environment env;

    public DbH2JdbcTemplateApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        System.getProperties().setProperty("spring.application.name", "DB H2 JdbcTemplate Application");
        SpringApplication app = new SpringApplication(DbH2JdbcTemplateApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);

        try {
            ConfigurableApplicationContext context = app.run(args);
            Environment env = context.getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
            runTest(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            Environment env = app.run(args).getEnvironment();
//            SpringUtil.logApplicationStartup(log, env);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void runTest(ApplicationContext ctx) {
//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//      return args -> {

        log.info("------------- zyme03 (Default) beans: ------------------------------------------------------------");
        DataSource dataSource = (DataSource) ctx.getBean(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT);
        log.info("{}:  {}", JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT, dataSource);

        TransactionManager transactionManager = (TransactionManager) ctx.getBean(JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT);
        log.info("{}:  {}", JdbcConfigurationDefaultSettings.TRANSACTION_MANAGER_DEFAULT, transactionManager);

        TransactionTemplate transactionTemplate = (TransactionTemplate) ctx.getBean(JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT);
        log.info("{}:  {}", JdbcConfigurationDefaultSettings.TRANSACTION_TEMPLATE_DEFAULT, transactionTemplate);

        JdbcTemplate jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
        log.info("jdbcTemplate:  {}", jdbcTemplate);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = (NamedParameterJdbcTemplate) ctx.getBean("namedParameterJdbcTemplate");
        log.info("namedParameterJdbcTemplate:  {}", namedParameterJdbcTemplate);

        JdbcTemplateInsertSqlService jdbcTemplateInsertSqlService = (JdbcTemplateInsertSqlService) ctx.getBean("jdbcTemplateInsertSqlService");
        log.info("jdbcTemplateInsertSqlService:  {}", jdbcTemplateInsertSqlService);

        JdbcTemplateUpdateSqlService jdbcTemplateUpdateSqlService = (JdbcTemplateUpdateSqlService) ctx.getBean("jdbcTemplateUpdateSqlService");
        log.info("jdbcTemplateUpdateSqlService:  {}", jdbcTemplateUpdateSqlService);

        JdbcTemplateDeleteSqlService jdbcTemplateDeleteSqlService = (JdbcTemplateDeleteSqlService) ctx.getBean("jdbcTemplateDeleteSqlService");
        log.info("jdbcTemplateDeleteSqlService:  {}", jdbcTemplateDeleteSqlService);

        JdbcTemplateSelectSqlService jdbcTemplateSelectSqlService = (JdbcTemplateSelectSqlService) ctx.getBean("jdbcTemplateSelectSqlService");
        log.info("jdbcTemplateSelectSqlService:  {}", jdbcTemplateSelectSqlService);

        JdbcTemplateInsertBatchSqlService jdbcTemplateInsertBatchSqlService = (JdbcTemplateInsertBatchSqlService) ctx.getBean("jdbcTemplateInsertBatchSqlService");
        log.info("jdbcTemplateInsertBatchSqlService:  {}", jdbcTemplateInsertBatchSqlService);

        JdbcTemplateAuthTestService jdbcTemplateAuthTestService = (JdbcTemplateAuthTestService) ctx.getBean("jdbcTemplateAuthTestService");
        log.info("jdbcTemplateAuthTestService:  {}", jdbcTemplateAuthTestService);

//if (1==1) {
////    User user;
////    user = jdbcTemplateAuthTestService.selectUser("john");
//    Authority authority;
//    authority = jdbcTemplateAuthTestService.selectAuthority("john");
//    return;
//}

        log.info("------------- jdbcTemplateInsertSqlService methods calls: ----------------------------------------");
        jdbcTemplateInsertSqlService.insertOneNoReturn("insertOneNoReturn()", null, LocalDateTime.now());

        log.info("------------- jdbcTemplateInsertBatchSqlService methods calls: -----------------------------------");
        jdbcTemplateInsertBatchSqlService.insertBatchList("insertBatchList()", "Batch 11 records:", LocalDateTime.now(), 11);
        jdbcTemplateInsertBatchSqlService.insertBatchListWithNamedParameter("insertBatchListWithNamedParameter()", "Batch 7 records:", LocalDateTime.now(), 7);

        log.info("------------- jdbcTemplateUpdateSqlService methods calls: ----------------------------------------");
        jdbcTemplateUpdateSqlService.update(1L, "jdbcTemplateUpdateSqlService", null, LocalDateTime.now());

        log.info("------------- jdbcTemplateDeleteSqlService methods calls: ----------------------------------------");
        jdbcTemplateDeleteSqlService.delete();

        log.info("------------- jdbcTemplateSelectSqlService methods calls: ----------------------------------------");
        jdbcTemplateSelectSqlService.selectOneColumnOneRow();
        jdbcTemplateSelectSqlService.selectOneRow();
        jdbcTemplateSelectSqlService.selectManyRows();

        log.info("------------- jdbcTemplateAuthTestService methods calls: -----------------------------------------");
        User user;
        user = jdbcTemplateAuthTestService.selectUser("john");
        user = jdbcTemplateAuthTestService.selectUser("john2");
        Authority authority;
        authority = jdbcTemplateAuthTestService.selectAuthority("john");
        authority = jdbcTemplateAuthTestService.selectAuthority("john2");

//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }

//      };
    }

}

