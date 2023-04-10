package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Kill extends SubCommandCompletor implements SubCommandExecutor {
    public Kill(){
        NodeBuilder nodeBuilder = new NodeBuilder(create("service",
                create("kill",
                        create("server", "proxy",
                                create(new ScreensNode())))));
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
                System.out.println(Chalk.on("[!] service kill server [" + LanguageManager.getMessage("name") + "]").red());
                System.out.println(Chalk.on("[!] service kill proxy [" + LanguageManager.getMessage("name") + "]").red());
                return true;
            }
            BundleData bundleData = Core.getInstance().getBundleManager().getBundleData(args[1]);

            String[] processName = args[2].split("-");
            IJVMExecutor jvmExecutor = Core.getInstance().getJvmContainer().getJVMExecutor(processName[0], bundleData);

            if(jvmExecutor == null){
                System.out.println(LanguageManager.getMessage("commands.service.kill.incorrectService"));
                return true;
            }

            int sId;
            try {
                sId =  Integer.parseInt(processName[1]);
            }catch (Exception e){
                System.out.println(LanguageManager.getMessage("commands.service.kill.serviceNotFound"));
                return true;
            }

            IService jvmService = jvmExecutor.getService(sId);
            jvmService.getProcess().destroy();
            return true;
        }



        return false;
    }
}
