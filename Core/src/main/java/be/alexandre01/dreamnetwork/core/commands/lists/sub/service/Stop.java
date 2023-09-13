package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Stop extends SubCommandCompletor implements SubCommandExecutor {
    public Stop(Command command){
        super(command);
        NodeBuilder nodeBuilder = new NodeBuilder(create(value,
                create("stop", create(new ScreensNode()),create("all",create(new BundlesNode(true,true,false))))));
        /*setCompletion(node("service",
                node("stop",
                        node("server", "proxy"))));
        addCompletor("service","stop","server");
        addCompletor("service","stop","proxy");*/
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("stop")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service stop server [" + Console.getFromLang("name") + "]").red());
                System.out.println(Chalk.on("[!] service stop proxy [" + Console.getFromLang("name") + "]").red());
                return true;
            }
         /*   JVMContainer.JVMType type;
            try {
                type = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type choosed is invalid... choose SERVER or PROXY").red());
                return true;
            }*/


            if(args[1].equalsIgnoreCase("all")){
                if(args.length < 3){
                    Console.printLang("commands.service.stop.incorrectService");
                    return true;
                }
                IJVMExecutor exec = Core.getInstance().getJvmContainer().tryToGetJVMExecutor(args[2]);

                if(exec == null){
                    Console.printLang("commands.service.stop.incorrectService");
                    return true;
                }
                //iService.removeService();
                new ArrayList<>(exec.getServices()).forEach(IService::stop);

                return true;
            }
            IService service = Core.getInstance().getJvmContainer().tryToGetService(args[1]);

            if(service == null){
                Console.printLang("commands.service.stop.incorrectService");
                return true;
            }
            service.stop();
            // service.removeService();
            return true;
        }



        return false;
    }
}
