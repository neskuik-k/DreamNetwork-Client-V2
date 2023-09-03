package be.alexandre01.dreamnetwork.api.installer;

import be.alexandre01.dreamnetwork.api.installer.ContentInstaller;
import lombok.SneakyThrows;

import java.io.File;
import java.util.List;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:09
*/
public interface IInstallerManager {
    @SneakyThrows
    boolean launchDependInstall(String version, File file, ContentInstaller.IInstall iInstall);

    @SneakyThrows
    boolean launchMultipleInstallation(String url, List<File> files, String name, ContentInstaller.IInstall iInstall);

    @SneakyThrows
    boolean launchInstallation(String url, File file, String name);
}
