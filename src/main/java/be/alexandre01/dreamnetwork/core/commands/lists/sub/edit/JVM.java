package be.alexandre01.dreamnetwork.core.commands.lists.sub.edit;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.jvm.JavaIndex;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import java.io.File;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class JVM extends SubCommandCompletor implements SubCommandExecutor {
    public JVM(){
        setCompletion(node("edit",
                node("jvm",
                        node("set","remove","list"))));

    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
            return false;
        }

        if(args[0].equalsIgnoreCase("jvm")){
            String name = Console.getFromLang("name");
            if(args.length < 2){
                System.out.println(Chalk.on("[!] edit jvm set [" + name + "] [" + Console.getFromLang("path") + "]").red());
                System.out.println(Chalk.on("[!] edit jvm remove [" + name + "]").red());
                System.out.println(Chalk.on("[!] edit jvm list").red());
                return true;
            }
            if(args[1].equalsIgnoreCase("set")){
                JavaIndex javaIndex = Core.getInstance().getJavaIndex();
                if(args.length < 4){
                    System.out.println(Chalk.on("[!] edit jvm set [" + name + "] [Path]").red());
                    return true;
                }

                File file = new File(Config.getPath(args[3]+"/bin/java"));
                if(!file.exists()){
                    Console.printLang("commands.edit.jvm.invalidJavaPath");
                    return true;
                }
                javaIndex.put(args[2].toLowerCase(),args[3]);
                javaIndex.refreshFile();
                return true;
            }
            if(args[1].equalsIgnoreCase("remove")){
                JavaIndex javaIndex = Core.getInstance().getJavaIndex();
                if(args.length < 3){
                    System.out.println(Chalk.on("[!] edit jvm remove [" + name + "]").red());
                    return true;
                }
                if(args[2].equalsIgnoreCase("default")){
                    Console.printLang("commands.edit.jvm.cantDeleteDefault");
                    return true;
                }

                javaIndex.remove(args[2].toLowerCase());
                javaIndex.refreshFile();
                return true;
            }
            if(args[1].equalsIgnoreCase("list")){

            }
        }



        return false;
    }
}
