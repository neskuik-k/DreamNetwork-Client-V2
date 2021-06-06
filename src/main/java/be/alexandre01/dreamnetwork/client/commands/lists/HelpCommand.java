package be.alexandre01.dreamnetwork.client.commands.lists;


import be.alexandre01.dreamnetwork.client.commands.Command;
import be.alexandre01.dreamnetwork.client.console.Console;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import com.github.tomaslanger.chalk.Chalk;
import jline.console.completer.StringsCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HelpCommand extends Command {

    public HelpCommand(String name) {
        super(name);

        setCompletions(new StringsCompleter("help"));

        commandExecutor = new CommandExecutor() {
            @Override
            public boolean execute(String[] args) {
                Console.print(Chalk.on("Lists of commands for help:").green().bold().underline());
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                Console.print(Chalk.on("How to manage a service ").underline());
                Console.print("service", Level.INFO);
                Console.print("add proxy [name] | add a proxy ", Level.INFO);
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());
                Console.print(Chalk.on("How to start or stop a server: ").underline());
                Console.print("start server [name] | start a server ", Level.INFO);
                Console.print("start proxy [name] | start a proxy ", Level.INFO);
                Console.debugPrint("");
                Console.print("stop server [name] | stop a server ", Level.INFO);
                Console.print("stop proxy [name] | stop a proxy ", Level.INFO);
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(Chalk.on("How to connect to the console: ").underline());
                Console.print("screen server [name] | connect to the server's console ", Level.INFO);
                Console.print("screen proxy [name] | connect to the proxy's console ", Level.INFO);

                Console.debugPrint(Chalk.on("   ------------------------------------------------------").yellow());

                Console.print(Chalk.on("How to remove server: ").underline());
                Console.print("remove server [name] | remove a server ", Level.INFO);
                Console.print("remove proxy [name] | remove a proxy ", Level.INFO);
                Console.debugPrint(Chalk.on("   ------------------------------------------------------").red());
                return true;
            }
        };
    }
    public static class HelpBuilder{
        private ArrayList<Object> sbs;
        private List<Integer> indexLoggerException = new ArrayList<>();
        private String commandName;
        public HelpBuilder(String commandName){
            this.commandName = commandName;
            sbs = new ArrayList<>();
            sbs.add((Chalk.on("Lists of commands for "+commandName+":").green().bold().underline()));
        }
        public HelpBuilder setCmdUsage(String usage, String... sub){
            StringBuilder sb = new StringBuilder();
            sb.append(Colors.ANSI_CYAN+commandName+Colors.ANSI_RESET()).append(" ");
            for(String s : sub)
                sb.append(s).append(" ");

            sbs.add(Chalk.on(sb.toString()+"| "+usage));
            return this;
        }
        public HelpBuilder setTitleUsage(String u){
            sbs.add(Chalk.on(u).underline());
            return this;
        }

        public HelpBuilder setLines(Colors colors){
            sbs.add(colors+"   ------------------------------------------------------");
            indexLoggerException.add(sbs.size()-1);
            return this;
        }
        private Object getLastSB(){
            return sbs.get(sbs.size()-1);
        }
        public int getSize(){
         return sbs.size();
        }
        public HelpBuilder build(){
            int i = 0;
            for(Object object : sbs){
                if(indexLoggerException.contains(i)){
                    Console.debugPrint(sbs);
                }else {
                    Console.print(object,Level.INFO);
                }
                i++;
            }
            return this;
        }

    }

}
