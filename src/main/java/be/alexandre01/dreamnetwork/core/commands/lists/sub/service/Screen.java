package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;


import java.util.concurrent.atomic.AtomicInteger;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Screen extends SubCommandCompletor implements SubCommandExecutor {
    public Screen(){
        NodeBuilder nodeBuilder = new NodeBuilder(create("service",
                create("screen",
                        create(new ScreensNode()))));
       /* setCompletion(node("service",
                node("screen")));*/
            addCompletor("service","screen","%screen%");
    }
    @Override
    public boolean onSubCommand(String[] args) {
        ScreenManager screenManager = ScreenManager.instance;
        if(!args[0].equalsIgnoreCase("screen")){
            System.out.println("");
            return false;
        }else {
            sendList(screenManager);
        }


        if(args.length == 1){
            notComplete();
            return true;
        }

        if(args[1].equalsIgnoreCase("refresh")){
            Console.printLang("commands.service.screen.refreshing");
            Console.printLang("commands.service.screen.tryRefresh");

            for (IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().jvmExecutors){
                for (IService service : jvmExecutor.getServices()) {
                    if(service.getScreen() == null){
                        new be.alexandre01.dreamnetwork.core.service.screen.Screen(service);
                        Console.printLang("commands.service.screen.backupingService", jvmExecutor.getName(), service.getId());
                    }else {
                        Console.printLang("commands.service.screen.alreadyBackuped", jvmExecutor.getName(), service.getId(),service.getScreen());
                    }
                }

            }
            return true;
        }


        if(screenManager.containsScreen(args[1])){
            screenManager.watch(args[1]);
        }else {
         sendList(screenManager);
        }
        return true;
    }

    private void notComplete(){
    }
    private void sendList(ScreenManager screenManager){
        System.out.println(Colors.GREEN_BOLD + "[*] Proxy ; "+ Colors.CYAN_BOLD+"[*] " + Console.getFromLang("server") + " ;");
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger(1);
        if(screenManager.getScreens().isEmpty()){
            Console.printLang("commands.service.screen.noScreen");
            return;
        }
        screenManager.getScreens().forEach((s, screen) -> {

            if(screen.getService().getJvmExecutor().isProxy()){
                sb.append(Colors.GREEN_BOLD);
            }else {
                sb.append(Colors.CYAN_BOLD);
            }
            sb.append(screen.getScreenName());
            if(i.get() != screenManager.getScreens().size())
                sb.append(", ");
            i.getAndIncrement();
        });

        Console.printLang("commands.service.screen.list", sb.toString());
    }
}
