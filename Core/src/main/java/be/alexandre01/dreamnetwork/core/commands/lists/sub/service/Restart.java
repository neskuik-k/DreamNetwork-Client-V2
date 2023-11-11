package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import lombok.NonNull;

import java.util.Optional;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Restart extends SubCommandCompletor implements SubCommandExecutor {
    public Restart(Command command){
        super(command);

        NodeContainer restart =  create("restart", create(new ScreensNode()));
        new NodeBuilder(create(value,restart));
        new NodeBuilder(restart);

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

        if(args[0].equalsIgnoreCase("restart")){
            if(args.length < 2){
                System.out.println(Colors.RED+"[!] service stop server [" + Console.getFromLang("name") + "]");
                System.out.println("[!] service stop proxy [" + Console.getFromLang("name") + "]");
                return true;
            }
         /*   JVMContainer.JVMType type;
            try {
                type = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type choosed is invalid... choose SERVER or PROXY").red());
                return true;
            }*/

            Optional<IService> service = Core.getInstance().getJvmContainer().tryToGetService(args[1]);
            if(!service.isPresent()){
                System.out.println(Colors.RED+"[!] The service choosed is invalid...");
                return true;
            }
            service.get().restart();

            return true;
        }



        return false;
    }
}
