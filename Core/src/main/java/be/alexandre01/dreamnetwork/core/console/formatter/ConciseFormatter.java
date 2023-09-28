package be.alexandre01.dreamnetwork.core.console.formatter;


import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.console.formatter.IConciseFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class ConciseFormatter extends Formatter implements IConciseFormatter {
        private final DateFormat date = new SimpleDateFormat( "HH:mm:ss");
        private final boolean coloured;

        String warningEmoji = Console.getEmoji("warning","",""," ");
        String errorEmoji = Console.getEmoji("stop_sign","",""," ");

        public ConciseFormatter(boolean coloured) {
            this.coloured = coloured;
        }

        @Override
        @SuppressWarnings("ThrowableResultIgnored")
        public String format(LogRecord record)
        {

            if(coloured){
            StringBuilder formatted = new StringBuilder();
            formatted.append(Colors.BLACK_BRIGHT_UNDERLINED+date.format( record.getMillis())+ Colors.RESET);
            formatted.append(Colors.BLUE+" [");

                appendLevel(formatted, record.getLevel() );



            formatted.append( Colors.BLUE+"] "+Colors.ANSI_RESET());
            ByteBuffer b = StandardCharsets.UTF_8.encode(formatMessage(record));
            new String(formatMessage( record ).getBytes(), StandardCharsets.UTF_8);
            String s = new String(b.array(), StandardCharsets.UTF_8);
            /*if(Config.isWindows())
                s =   Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");*/
            formatted.append(s);
            formatted.append( '\n' );

            if ( record.getThrown() != null )
            {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace( new PrintWriter( writer ) );
                formatted.append( writer );
            }

            return formatted.toString();
            }
                StringBuilder formatted = new StringBuilder();
                formatted.append(date.format( record.getMillis() ));
                formatted.append(" [");

                formatted.append(record.getLevel());



                formatted.append("] ");
                ByteBuffer b = StandardCharsets.UTF_8.encode(formatMessage(record));
                new String(formatMessage( record ).getBytes(), StandardCharsets.UTF_8);
                String s = new String(b.array(), StandardCharsets.UTF_8);
                s =   Normalizer.normalize(s, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "").replaceAll("[^\\x00-\\x7f]","");
                formatted.append(s);
                formatted.append( '\n' );

                if ( record.getThrown() != null )
                {
                    StringWriter writer = new StringWriter();
                    record.getThrown().printStackTrace( new PrintWriter( writer ) );
                    formatted.append( writer );
                }

                return formatted.toString();

        }

        private void appendLevel(StringBuilder builder, Level level)
        {

            if ( !coloured )

            {
                builder.append( level.getLocalizedName() );
                return;
            }

            if ( level == Level.INFO )
            {
                builder.append(Colors.GREEN_BOLD+level.getLocalizedName());
            } else if ( level == Level.WARNING )
            {
                builder.append(warningEmoji+Colors.YELLOW_BOLD+level.getLocalizedName());
            } else if ( level == Level.SEVERE )
            {
                builder.append(errorEmoji+ Colors.RED_BOLD+level.getLocalizedName());
            } else if(level == Level.FINE){
                builder.append( Colors.RED_BACKGROUND+Colors.WHITE_BOLD+"DEBUG");
            }else
            {
                builder.append( Colors.CYAN_BOLD+level.getLocalizedName());
            }


        }

}
