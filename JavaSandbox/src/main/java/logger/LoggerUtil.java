package logger;

import java.io.IOException;
import java.util.logging.*;

@SuppressWarnings("WeakerAccess")
public class LoggerUtil {

    private static final String logFileName = "sandbox.log";

    static public void setup() throws IOException {
        // Get the global logger to configure it.
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // Suppress the logging output to the console.
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        // Configure the logger.
        logger.setLevel(Level.FINEST);
        FileHandler fileHandler = new FileHandler(logFileName);
        fileHandler.setFormatter(new BasicFormatter());
        logger.addHandler(fileHandler);
    }
}