package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;
import static org.jline.builtins.Completers.TreeCompleter.node;

public class Stop extends SubCommandCompletor implements SubCommandExecutor {
    public Stop(){
        NodeBuilder nodeBuilder = new NodeBuilder(create("service",
                create("stop",
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

        if(args[0].equalsIgnoreCase("stop")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service stop server [name]").red());
                System.out.println(Chalk.on("[!] service stop proxy [name]").red());
                return true;
            }
            JVMContainer.JVMType type;
            try {
                type = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type choosed is invalid... choose SERVER or PROXY").red());
                return true;
            }

            String[] processName = args[2].split("-");
            IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(processName[0], type);

            if(jvmExecutor == null){
                System.out.println(Chalk.on("[!] The service mentionned is not correct..").red());
                return true;
            }

            int sId;
            try {
                sId =  Integer.parseInt(processName[1]);
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The service id is not findable").red());
                return true;
            }

            IService jvmService = jvmExecutor.getService(sId);
            jvmService.stop();
            jvmService.removeService();
            return true;
        }



        return false;
    }
}
