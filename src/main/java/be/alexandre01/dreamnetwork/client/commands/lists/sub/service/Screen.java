package be.alexandre01.dreamnetwork.client.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandExecutor;
import be.alexandre01.dreamnetwork.client.console.colors.Colors;
import be.alexandre01.dreamnetwork.client.service.screen.ScreenManager;


import java.util.concurrent.atomic.AtomicInteger;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class Screen extends SubCommandCompletor implements SubCommandExecutor {
    public Screen(){
        setCompletion(node("service",
                node("screen")));
            addCompletor("service","screen");
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
