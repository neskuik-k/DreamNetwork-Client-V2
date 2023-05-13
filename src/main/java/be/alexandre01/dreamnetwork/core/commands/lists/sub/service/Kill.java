package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Kill extends SubCommandCompletor implements SubCommandExecutor {
    public Kill(){
        NodeBuilder nodeBuilder = new NodeBuilder(create("service",
                create("kill", create(new ScreensNode()))));
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
            BundleData bundleData = Core.getInstance().getBundleManager().getBundleData(args[1]);


            IService service = Core.getInstance().getJvmContainer().tryToGetService(args[1]);
            if(service == null){
                Console.printLang("commands.service.kill.incorrectService");
                return true;
            }

            service.getProcess().destroy();
            return true;
        }



        return false;
    }
}
