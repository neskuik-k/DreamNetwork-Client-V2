package be.alexandre01.dreamnetwork.api.installer;


import be.alexandre01.dreamnetwork.client.installer.Installer;

import java.io.File;
import java.util.List;


public class ContentInstaller {
    public static void install(String url, List<File> fileList, String name, IInstall install) {
        Installer.launchMultipleInstallation(url, fileList,name,install);
    }
    public interface IInstall{
        public void start();
        public void complete();
    }
}
