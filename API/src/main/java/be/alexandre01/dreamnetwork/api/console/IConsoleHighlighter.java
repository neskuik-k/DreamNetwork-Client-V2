package be.alexandre01.dreamnetwork.api.console;

import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 21:22
*/
public interface IConsoleHighlighter extends Highlighter {

    @Override
    AttributedString highlight(LineReader lineReader, String s);

    void print(AttributedStringBuilder asb, String s);

    @Override
    void setErrorPattern(Pattern pattern);

    @Override
    void setErrorIndex(int i);

    void setEnabled(boolean enabled);
}
