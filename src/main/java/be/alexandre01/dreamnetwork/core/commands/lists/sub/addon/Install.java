package be.alexandre01.dreamnetwork.core.commands.lists.sub.addon;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.core.addons.AddonDowloaderObject;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.utils.files.CDNFiles;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.logging.Level;

public class Install extends SubCommand {
    private final HashMap<String, AddonDowloaderObject> addons;

    public Install(){
        addons = CDNFiles.getAddons();
        NodeBuilder nodeBuilder = new NodeBuilder(
                NodeBuilder.create("addon", NodeBuilder.create("install", NodeBuilder.create(addons.keySet().toArray())))
        );
    }

    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        return when(sArgs-> {
            if(sArgs.length < 2 || !addons.containsKey(sArgs[1])){
                Console.printLang("commands.addon.invalidName");
                return true;
            }
            try {
                URL addonURL = new URL(addons.get(sArgs[1]).getDownloadLink());
                InputStream in = addonURL.openStream();
                Console.printLang("installer.startInstallation");
                Files.copy(in, Paths.get("addons/" + sArgs[1] + ".jar"), StandardCopyOption.REPLACE_EXISTING);
                Console.printLang("commands.addon.install.completed", sArgs[1]);
            }catch (IOException e){
                Console.printLang("commands.addon.install.error", Level.WARNING);
                return false;
            }
            return true;
        },args,"install","[" + Console.getFromLang("name") + "]");
    }
}
