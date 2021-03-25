package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

public class Start implements SubCommandExecutor {
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("start")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service start server [name]").red());
                return true;
            }
            JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[1]);
            if(jvmExecutor == null){
                System.out.println(Chalk.on("[!] The service mentionned is not configurated..").red());
                return true;
            }
            jvmExecutor.startServer();
        }



        return false;
    }
}
