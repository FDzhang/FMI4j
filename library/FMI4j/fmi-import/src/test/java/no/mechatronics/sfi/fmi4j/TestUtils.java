package no.mechatronics.sfi.fmi4j;

public class TestUtils {

    public static String getTEST_FMUs() {
        String env = System.getenv("TEST_FMUs");
        if (env == null) {
            throw new IllegalStateException("TEST_FMUs not found on PATH!");
        }
        return env;
    }

}
