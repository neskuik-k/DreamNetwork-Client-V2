package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.client.Client;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.client.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.service.JVMContainer;
import be.alexandre01.dreamnetwork.client.service.JVMExecutor;
import com.github.tomaslanger.chalk.Chalk;
import lombok.NonNull;

public class Start extends SubCommandCompletor implements SubCommandExecutor {
    public Start(){
        addCompletor("service","start","server");
        addCompletor("service","start","proxy");

    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(args.length < 1){
          return false;
        }

        if(args[0].equalsIgnoreCase("start")){
            if(args.length < 2){
                System.out.println(Chalk.on("[!] service start server [name]").red());
                System.out.println(Chalk.on("[!] service start proxy [name]").red());
                return true;
            }
            JVMContainer.JVMType type;
            try {
                type = JVMContainer.JVMType.valueOf(args[1].toUpperCase());
            }catch (Exception e){
                System.out.println(Chalk.on("[!] The type choosed is invalid... choose SERVER or PROXY").red());
                return true;
            }


            JVMExecutor jvmExecutor = Client.getInstance().getJvmContainer().getJVMExecutor(args[2], type);

            if(jvmExecutor == null){
                System.out.println(Chalk.on("[!] The service mentionned is not configurated..").red());
                return true;
            }
            jvmExecutor.startServer();
            return true;
        }



        return false;
    }
}
