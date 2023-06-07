package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Restart extends SubCommandCompletor implements SubCommandExecutor {
    public Restart(Command command){
        super(command);
        NodeBuilder nodeBuilder = new NodeBuilder(create(value,
                create("restart",
                        create("server", "proxy",
                                create(new ScreensNode())))));
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

            String[] processName = args[2].split("-");
            IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(processName[0], args[1]);

            if(jvmExecutor == null){
                Console.printLang("commands.service.stop.incorrectService");
                return true;
            }

            int sId;
            try {
                sId =  Integer.parseInt(processName[1]);
            }catch (Exception e){
                Console.printLang("commands.service.stop.idNotFound");
                return true;
            }

            IService jvmService = jvmExecutor.getService(sId);
            jvmService.restart();
            return true;
        }



        return false;
    }
}
