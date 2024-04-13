package ua.mai.zyme.spring.log;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.spring.log",
})
public class LogConfig {

    @Bean
    public LogService loggingService() {
        return new LogServiceImpl("GraphQL Library");
    }

//    @Bean
//    public CommonsRequestLoggingFilter requestLoggingFilter() {
//        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter ();
//        loggingFilter.setIncludeQueryString(true);
//        loggingFilter.setIncludeHeaders(true);
//        loggingFilter.setIncludePayload(true);
//        loggingFilter.setMaxPayloadLength(10000);
//        loggingFilter.setIncludeHeaders(false);
//        loggingFilter.setAfterMessagePrefix("REQUEST DATA: ");
//        return loggingFilter;
//    }

//    @Bean
//    public ServletRegistrationBean dispatcherRegistration() {
//        return new ServletRegistrationBean(dispatcherServlet());
//    }
//
//    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
//    public DispatcherServlet dispatcherServlet() {
//        return new LoggableDispatcherServlet();
//    }

}