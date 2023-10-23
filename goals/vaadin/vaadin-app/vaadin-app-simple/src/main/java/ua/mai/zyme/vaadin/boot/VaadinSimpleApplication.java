package ua.mai.zyme.vaadin.boot;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.vaadin.artur.helpers.LaunchUtil;
import ua.mia.zyme.common.DefaultProfileUtil;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
        (scanBasePackageClasses = {
//                AaClientConfiguration.class,
        }, exclude = {
                SecurityAutoConfiguration.class,
                ErrorMvcAutoConfiguration.class
        })
@EnableConfigurationProperties
        ({
//                AaClientProperties.class,
        })
@Theme("custom-theme")
public class VaadinSimpleApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    private static final Logger log = LoggerFactory.getLogger(VaadinSimpleApplication.class);

    private final Environment env;

    public VaadinSimpleApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application.class, args));
        SpringApplication app = new SpringApplication(VaadinSimpleApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
//        Environment env = app.run(args).getEnvironment();
        LaunchUtil.launchBrowserInDevelopmentMode(app.run(args));
//        logApplicationStartup(env);
    }

    @Override
    public void configurePage(AppShellSettings settings)  {
//        settings.addInlineFromFile(InitialPageSettings.Position.PREPEND,
//                "inline.js", InitialPageSettings.WrapMode.JAVASCRIPT);
//
//        settings.addMetaTag("og:title", "The Rock");
//        settings.addMetaTag("og:type", "video.movie");
//        settings.addMetaTag("og:url",
//                "http://www.imdb.com/title/tt0117500/");
//        settings.addMetaTag("og:image",
//                "http://ia.media-imdb.com/images/rock.jpg");

        settings.addLink("shortcut icon", "VAADIN/static/themes/custom-theme/favicon.ico");
//        Image icon = new Image("VAADIN/static/themes/custom-theme/img/tif_common/error.gif", "");

//        settings.addFavIcon("icon", "src/main/resources/META-INF/resources/themes/custom-theme/favicon.ico", "192x192");
    }
}
