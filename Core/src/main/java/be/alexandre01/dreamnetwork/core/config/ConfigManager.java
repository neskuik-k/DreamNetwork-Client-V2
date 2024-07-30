package be.alexandre01.dreamnetwork.core.config;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 19:46
*/

import be.alexandre01.dreamnetwork.api.config.GlobalSettings;
import be.alexandre01.dreamnetwork.api.config.IConfigManager;
import be.alexandre01.dreamnetwork.api.config.IFileCopyAsync;
import be.alexandre01.dreamnetwork.api.console.language.ILanguageManager;
import be.alexandre01.dreamnetwork.api.installer.IInstallerManager;
import be.alexandre01.dreamnetwork.api.service.FileDispatcher;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;

public class ConfigManager implements IConfigManager {
    public GlobalSettings getGlobalSettings(){
        return Main.getGlobalSettings();
    }

    public ILanguageManager getLanguageManager(){
        return Main.getLanguageManager();
    }

    @Override
    public FileDispatcher getFileDispatcher() {
        return Main.getBundlesLoading().getFileDispatcher();
    }

    @Override
    public IInstallerManager getInstallerManager() {
        return Core.getInstance().getInstallerManager();
    }

    @Override
    public String getUsername() {
        return Main.getUsername();
    }

    @Override
    public boolean isDebug() {
        return Core.getInstance().isDebug();
    }

    public IFileCopyAsync getFileCopyAsync(){
        return Main.getFileCopyAsync();
    }
}
