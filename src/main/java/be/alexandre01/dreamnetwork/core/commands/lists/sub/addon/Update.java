package be.alexandre01.dreamnetwork.core.commands.lists.sub.addon;

import be.alexandre01.dreamnetwork.api.commands.Command;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.Console;
import lombok.NonNull;

import java.util.List;

public class Update extends SubCommand {
    private List<String> addonsToUpdate = null;

    public Update(Command command){
        super(command);
       /* if(Main.getCdnFiles().isInstanced()) {
            addonsToUpdate = Main.getCdnFiles().getAddonsToUpdate();
            addonsToUpdate.add("add");
            NodeBuilder nodeBuilder = new NodeBuilder(
                    NodeBuilder.create("addon",
                            NodeBuilder.create("update",
                                    NodeBuilder.create(addonsToUpdate.toArray()))));
            addonsToUpdate.remove("all");
        }*/
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        return when(sArgs -> {
           /* if(!Main.getCdnFiles().isInstanced()){
                Console.printLang("commands.addon.cantGetOfficialAddon");
                return true;
            }
            if(addonsToUpdate == null){
                addonsToUpdate = Main.getCdnFiles().getAddonsToUpdate();
                NodeBuilder nodeBuilder = new NodeBuilder(
                        NodeBuilder.create("addon",
                                NodeBuilder.create("update",
                                        NodeBuilder.create("all", (!addonsToUpdate.isEmpty() ? addonsToUpdate.get(0) : "")))));
            }
            if(sArgs.length < 2){
                Console.printLang("commands.addon.update.invalidArguments");
                if(addonsToUpdate.size() > 0){
                    addonsToUpdate.forEach(name -> {
                        Console.printLang("addons.canUpdate", name, name);
                    });
                }
                return true;
            }
            if(sArgs[1].equalsIgnoreCase("all")){
                if(addonsToUpdate.size() == 0){
                    Console.printLang("commands.addon.update.noAddonToUpdate");
                    return true;
                }
                addonsToUpdate.forEach(name -> {
                    String downloadCmd = "addon install " + name;
                    Main.getCommandReader().getConsole().getConsoleAction().listener(downloadCmd.split(" "));
                    addonsToUpdate.remove(name);
                });
            }
            if(!addonsToUpdate.contains(sArgs[1])){
                Console.printLang("commands.addon.update.addonNotFound", sArgs[1]);
                return true;
            }
            String downloadCmd = "addon install " + sArgs[1];
            Main.getCommandReader().getConsole().getConsoleAction().listener(downloadCmd.split(" "));
            addonsToUpdate.remove(sArgs[1]);*/
            return true;
        },args,"update","[" + Console.getFromLang("name") + "/all]");
    }
}
