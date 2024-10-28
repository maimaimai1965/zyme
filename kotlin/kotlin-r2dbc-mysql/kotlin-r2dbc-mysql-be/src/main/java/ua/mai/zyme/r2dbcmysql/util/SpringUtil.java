package ua.mai.zyme.r2dbcmysql.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class SpringUtil {

    public static void logApplicationStartup(Logger logger, Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("The host name could not be determined, using `localhost` as fallback");
        }
        logger.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n" +
                        "\tLocal: \t\t{}://localhost:{}{}\n" +
                        "\tExternal: \t{}://{}:{}{}\n" +
                        "\tProfile(s): {}\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                env.getActiveProfiles());
    }

}
