package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.utils.optional.Facultative;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;

import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Optional;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Kill extends SubCommandCompletor implements SubCommandExecutor {
    public Kill(Command command){
        super(command);
        NodeContainer kill =  create("kill", create(new ScreensNode()),create("all",create(new BundlesNode(true,true,false))));
        new NodeBuilder(create(value, kill));
        new NodeBuilder(kill);
        /*setCompletion(node("service",
                node("kill",
                        node("server", "proxy"))));
        addCompletor("service","kill","server");
        addCompletor("service","kill","proxy");*/

    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("kill")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service kill server [" + Console.getFromLang("name") + "]").red());
                System.out.println(Chalk.on("[!] service kill proxy [" + Console.getFromLang("name") + "]").red());
                return true;
            }
          //  BundleData bundleData = Core.getInstance().getBundleManager().getBundleData(args[1]);

            if(args[1].equalsIgnoreCase("all")){
                if(args.length < 3){
                    Console.printLang("commands.service.stop.incorrectService");
                    return true;
                }
                Optional<IExecutor> exec = Core.getInstance().getJvmContainer().findExecutor(args[2]);

                if(!exec.isPresent()){
                    Console.printLang("commands.service.stop.incorrectService");
                    return true;
                }
                new ArrayList<>(exec.get().getServices()).forEach(IService::kill);
                return true;
            }
            //Optional<IService> service = Core.getInstance().getJvmContainer().findService(args[1]);
            Facultative.ifPresentOrElse(Core.getInstance().getJvmContainer().findService(args[1]),
                    iService -> iService.getProcess().destroy(),
                    () -> Console.printLang("commands.service.kill.incorrectService"));
           /* if(service.isPresent()){
                service.get().getProcess().destroy();
            }else {
                Console.printLang("commands.service.kill.incorrectService");
            }*/


            return true;
        }



        return false;
    }
}
