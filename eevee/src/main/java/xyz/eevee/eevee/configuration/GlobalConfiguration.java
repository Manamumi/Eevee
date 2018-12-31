package xyz.eevee.eevee.configuration;

public class GlobalConfiguration {
    public static final String GOOGLE_API_CRED_ENV_VAR_NAME = "GOOGLE_APPLICATION_CREDENTIALS";
    public static final String COFFEE_HOST = "127.0.0.1";
    public static final String INSIDE_APP_TOKEN = System.getenv("INSIDE_APP_TOKEN");
    public static final int COFFEE_PORT = 7733;
    public static final int COFFEE_CACHE_TTL_SECONDS = 300;
    public static final String BUILD_INFO_PATH = "build.json";
    public static final String BUILD_SHA_NAME = "ciCommitSHA";
    public static final String BUILD_COMMIT_MESSAGE_NAME = "ciCommitMessage";
    public static final String BUILD_JOB_ID_NAME = "ciJobID";
    public static final String BUILD_BY_USER_NAME = "gitlabUserLogin";
    public static final String BUILD_BY_NAME_NAME = "gitlabUserName";
    public static final String BUILD_BY_ID_NAME = "gitlabUserID";
    public static final String BUILD_TIMESTAMP_NAME = "buildTime";
    public static final String CONFIG_OVERRIDE_PATH = "eevee/conf/Eevee.json";
    public static final String REMINDER_OPT_OUT_LIST_KEY = "eevee.remindSomebodyOptOutList";
    public static final String GHOST_OPT_OUT_LIST_KEY = "eevee.ghostOptOutList";
    public static final String COFFEE_PING_KEY = "eevee.coffeeDoNotCache";
}
