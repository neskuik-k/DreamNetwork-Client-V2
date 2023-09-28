package be.alexandre01.dreamnetwork.api.config;

import be.alexandre01.dreamnetwork.api.console.IConsoleManager;
import be.alexandre01.dreamnetwork.api.console.language.ILanguageManager;
import be.alexandre01.dreamnetwork.api.installer.IInstallerManager;
import be.alexandre01.dreamnetwork.api.service.FileDispatcher;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 19:46
*/
public interface IConfigManager {
    public GlobalSettings getGlobalSettings();
    public IFileCopyAsync getFileCopyAsync();

    public ILanguageManager getLanguageManager();

    public FileDispatcher getFileDispatcher();

    public IInstallerManager getInstallerManager();

    String getUsername();
    boolean isDebug();

}
