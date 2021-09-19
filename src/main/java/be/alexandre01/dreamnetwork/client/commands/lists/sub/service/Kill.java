package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMService;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Kill extends SubCommandCompletor implements SubCommandExecutor {
    public Kill(){
        setCompletion(node("service",
                node("kill",
                        node("server", "proxy"))));
        addCompletor("service","kill","server");
        addCompletor("service","kill","proxy");

    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("kill")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service kill server [name]").red());
                System.out.println(Chalk.on("[!] service kill proxy [name]").red());
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
            JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(processName[0], type);

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

            JVMService jvmService = jvmExecutor.getService(sId);
            jvmService.getProcess().destroy();
            return true;
        }



        return false;
    }
}
