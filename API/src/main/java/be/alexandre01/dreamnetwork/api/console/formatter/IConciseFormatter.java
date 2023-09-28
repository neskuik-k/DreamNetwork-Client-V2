package be.alexandre01.dreamnetwork.api.console.formatter;

import java.util.logging.LogRecord;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 21:19
*/
public interface IConciseFormatter {
    @SuppressWarnings("ThrowableResultIgnored")
    String format(LogRecord record);
}
