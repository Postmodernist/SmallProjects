package logger;

import java.io.IOException;
import java.util.logging.Logger;

public class LogUse {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        try {
            LoggerUtil.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.severe("hello");
        LOG.info("world");
    }
}
