package logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss.SSS");
    private Date date = new Date();

    @Override
    public String format(LogRecord record) {
        String timeStamp = calcDate(record.getMillis());

        char level = record.getLevel().getName().charAt(0);

        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }

        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return timeStamp + " " + level + " " + source + " " + formatMessage(record) + throwable + "\n";
    }

    private String calcDate(long millis) {
        date.setTime(millis);
        return dateFormat.format(date);
    }
}
