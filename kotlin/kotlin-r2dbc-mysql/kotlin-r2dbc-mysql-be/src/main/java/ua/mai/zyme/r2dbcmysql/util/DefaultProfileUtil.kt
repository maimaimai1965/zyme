package ua.mai.zyme.r2dbcmysql.util

import org.springframework.boot.SpringApplication
import org.springframework.core.env.Environment

/**
 * Utility class to load a Spring profile to be used as default
 * when there is no `spring.profiles.active` set in the environment or as command line argument.
 * If the value is not available in `application.yml` then `dev` profile will be used as default.
 */
object DefaultProfileUtil {

    private const val SPRING_PROFILE_DEFAULT = "spring.profiles.default"
    private const val SPRING_PROFILE_DEVELOPMENT = "dev"

    /**
     * Set a default to use when no profile is configured.
     *
     * @param app the Spring application
     */
    fun addDefaultProfile(app: SpringApplication) {
        val defProperties = mapOf(SPRING_PROFILE_DEFAULT to SPRING_PROFILE_DEVELOPMENT)
        /*
         * The default profile to use when no other profiles are defined
         * This cannot be set in the `application.yml` file.
         * See https://github.com/spring-projects/spring-boot/issues/1219
         */
        app.setDefaultProperties(defProperties)
    }

    /**
     * Get the profiles that are applied else get default profiles.
     *
     * @param env spring environment
     * @return profiles
     */
    fun getActiveProfiles(env: Environment): Array<String> {
        val profiles = env.activeProfiles
        return if (profiles.isEmpty()) env.defaultProfiles else profiles
    }

}