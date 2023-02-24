package be.alexandre01.dreamnetwork.core.console.formatter;


import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;

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


public class ConciseFormatter extends Formatter {
        private final DateFormat date = new SimpleDateFormat( "HH:mm:ss");
        private final boolean coloured;

        public ConciseFormatter(boolean coloured) {
            this.coloured = coloured;
        }

        @Override
        @SuppressWarnings("ThrowableResultIgnored")
        public String format(LogRecord record)
        {

            if(coloured){
            StringBuilder formatted = new StringBuilder();
            formatted.append(Chalk.on(Colors.BLACK_BRIGHT_UNDERLINED+date.format( record.getMillis())+ Colors.RESET));
            formatted.append(Chalk.on(" [").blue());

                appendLevel(formatted, record.getLevel() );



            formatted.append( Chalk.on("] ").blue());
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
                builder.append( Chalk.on(level.getLocalizedName()).bold().green());
            } else if ( level == Level.WARNING )
            {
                builder.append( Chalk.on(level.getLocalizedName()).bold().yellow());
            } else if ( level == Level.SEVERE )
            {
                builder.append( Chalk.on(level.getLocalizedName()).bold().red());
            } else if(level == Level.FINE){
                builder.append( Chalk.on("DEBUG").bgRed().white().bold());
            }else
            {
                builder.append( Chalk.on(level.getLocalizedName()).cyan().bold());
            }


        }

}
