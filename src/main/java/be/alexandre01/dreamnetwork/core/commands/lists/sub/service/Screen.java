package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.api.commands.sub.types.ScreensNode;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.api.service.IService;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.screen.ScreenManager;


import java.util.concurrent.atomic.AtomicInteger;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;
import static org.jline.builtins.Completers.TreeCompleter.node;

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
            return false;
        }else {
            sendList(screenManager);
        }


        if(args.length == 1){
            notComplete();
            return true;
        }

        if(args[1].equalsIgnoreCase("refresh")){
            System.out.println(Colors.BLUE+"Try to refresh the screens");
            for (IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().jvmExecutorsServers.values()){
                for (IService service : jvmExecutor.getServices()) {
                    if(service.getScreen() == null){
                        new be.alexandre01.dreamnetwork.core.service.screen.Screen(service);
                        System.out.println(Colors.BLUE+" Backuping screen for service on "+jvmExecutor.getName()+"-"+service.getId()+"...");
                    }else {
                        System.out.println(Colors.BLUE+" Screen for service on "+jvmExecutor.getName()+"-"+service.getId()+" is already backuped + " + service.getScreen());
                    }
                }

            }
            for (IJVMExecutor jvmExecutor : Core.getInstance().getJvmContainer().jvmExecutorsProxy.values()){
                for (IService service : jvmExecutor.getServices()) {
                    if(service.getScreen() == null){
                        new be.alexandre01.dreamnetwork.core.service.screen.Screen(service);
                        System.out.println(Colors.BLUE+" Backuping screen for service on "+jvmExecutor.getName()+"-"+service.getId()+"...");
                    }else {
                        System.out.println(Colors.BLUE+" Screen for service on "+jvmExecutor.getName()+"-"+service.getId()+" is already backuped + " + service.getScreen());
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
        System.out.println(Colors.GREEN_BOLD + "[*] Proxy ; "+ Colors.CYAN_BOLD+"[*] Server ;");
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger(1);
        if(screenManager.getScreens().isEmpty()){
            System.out.println("Il n'y a aucun screen actuellement allumé");
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

        System.out.println("Voici la liste des screens disponibles -> " + sb.toString());
    }
}
