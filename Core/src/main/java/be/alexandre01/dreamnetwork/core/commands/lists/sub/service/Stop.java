package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Stop extends SubCommandCompletor implements SubCommandExecutor {
    public Stop(Command command){
        super(command);

        NodeContainer stop =  create("stop", create(new ScreensNode()),create("all",create(new BundlesNode(true,true,false))));
        new NodeBuilder(create(value, stop));
        new NodeBuilder(stop);
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
                ArrayList<IService> services = null;
                if(args.length < 3){
                    if(Core.getInstance().getJvmContainer().getExecutors().stream().map(IExecutor::getServices).allMatch(Collection::isEmpty)){
                        Console.printLang("commands.service.stop.noService");
                        return true;
                    }
                    services = new ArrayList<>();
                    Collection<IExecutor> executors = Core.getInstance().getJvmContainer().getExecutors();
                    for(IExecutor executor : executors){
                        services.addAll(executor.getServices());
                    }
                    Console.printLang("commands.service.stop.all");
                }

                if(services == null){
                    if(args.length == 4){
                        System.out.println("Please specify an executor name");
                        return true;
                    }
                    Optional<IExecutor> exec = Core.getInstance().getJvmContainer().findExecutor(args[2]);
                    if(!exec.isPresent()){
                        Console.printLang("commands.service.stop.incorrectExecutor");
                        return true;
                    }
                    //iService.removeService();
                    services = new ArrayList<>(exec.get().getServices());
                }

                ArrayList<IService> finalServices = services;
                Console.getCurrent().addOverlay(new Console.Overlay() {
                    @Override
                    public void on(String data) {
                        disable();
                        if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                            finalServices.forEach(IService::stop);
                            return;
                        }
                    }
                }, Colors.YELLOW+"Are you sure you want to stop all the services ? [Y/N] > ");
                return true;
            }
            Optional<IService> service = Core.getInstance().getJvmContainer().findService(args[1]);

            if(!service.isPresent()){
                Console.printLang("commands.service.stop.incorrectService");
                return true;
            }
            service.get().stop();
            // service.removeService();
            return true;
        }



        return false;
    }
}
